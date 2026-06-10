import { readdir, lstat } from 'node:fs/promises';
import { resolve } from 'node:path';
import { Histogram } from "./Histogram.js";

// Prepares the report and starts the recursive scanning of the directory
export async function getFSReport(directory, maxFileSize, numBands) {
    const report = new Histogram(numBands, maxFileSize);
    await scanDirectory(directory, report);
    return report;
}

// Recursively scans the directory and updates the report when encoutering files
async function scanDirectory(dir, report) {
    let path = resolve(dir);
    let entries;
    try {
        entries = await readdir(path);   
    } catch (err) {
        report.updateDirectoriesSkipped()
        return
    }
    
    // Get file stats for all entries in parallel
    const statPromises = entries.map(async (entry) =>{
        try {
            let stat = await lstat(resolve(path, entry));
            return { entry, stat, path };
        } catch (err) {
            return { entry, stat: null, path };
        }
    });
    const stats = await Promise.all(statPromises);
    
    // Separate files and directories, then handle recursively in parallel
    const subdirPromises = [];
    
    for (const { entry, stat } of stats) {
        if (!stat) {
            report.updateDirectoriesSkipped();
            continue;
        }
 
        if (stat.isFile()) {
            report.update(stat.size);
        } 
        else if (stat.isDirectory()) {
            const entryPath = resolve(path, entry)
            report.updateDirectories();
            subdirPromises.push(scanDirectory(entryPath, report));
        }
    }
    
    // Wait for all subdirectories to be scanned in parallel
    await Promise.all(subdirPromises);
}

