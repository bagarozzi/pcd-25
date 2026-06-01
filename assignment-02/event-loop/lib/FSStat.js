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
    const entries = await readdir(path);
    for (const entry of entries) {
        const entryPath = resolve(path, entry);
        const fileStat = await lstat(entryPath);
        if (fileStat.isFile()) {
            report.update(fileStat.size);
        } 
        else if (fileStat.isDirectory()) {
            await scanDirectory(entryPath, report);
        }
    }
}

