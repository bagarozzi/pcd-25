import { readdir, lstat } from 'node:fs/promises';
import { resolve } from 'node:path';
import { Histogram } from "./Histogram.js";
import { DynamicFSReport } from "./DynamicFSReport.js";

// Prepares the report and starts the recursive scanning of the directory
export async function getFSReport(directory, maxFileSize, numBands) {
    const report = new Histogram(numBands, maxFileSize);
    await scanDirectory(directory, report);
    return report;
}

/**
 * Returns a DynamicFSReport that can be used to get progressive updates of the
 * scanned directory.
*/
export function getInteractiveFSReport(directory, maxFileSize, numBands) {
    return new DynamicFSReport(directory, numBands, maxFileSize);
}

// Recursively scans the directory and updates the report when encoutering files
async function scanDirectory(dir, report) {
    let path = resolve(dir);
    const entries = await readdir(path);
    
    // Get file stats for all entries in parallel
    const statPromises = entries.map(entry => 
        lstat(resolve(path, entry)).then(stat => ({ entry, stat, path }))
    );
    const stats = await Promise.all(statPromises);
    
    // Separate files and directories, then handle recursively in parallel
    const subdirPromises = [];
    
    for (const { entry, stat } of stats) {
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

