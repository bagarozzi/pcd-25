import { readdir, lstat } from 'node:fs/promises';
import { resolve } from 'node:path';
import { Histogram } from "./Histogram.js";

export class DynamicFSReport {
    
    constructor(directory, numBands, maxFileSize) {
        this.directories = [directory];
        this.numBands = numBands;
        this.maxFileSize = maxFileSize;
        this.report = new Histogram(numBands, maxFileSize);
    }

    async getNextUpdate() {
        const path = resolve(this.directories.shift());
        const entries = await readdir(path);
        
        // Get file stats for all entries in parallel
        const statPromises = entries.map(entry => 
            lstat(resolve(path, entry)).then(stat => ({ entry, stat, path }))
        );
        const stats = await Promise.all(statPromises);
        
        // Separate files and directories, then handle recursively in parallel
        const subDirs = [];
        
        for (const { entry, stat } of stats) {
            if (stat.isFile()) {
                this.report.update(stat.size);
            } 
            else if (stat.isDirectory()) {
                const entryPath = resolve(path, entry);
                this.report.updateDirectories();
                subDirs.push(entryPath);
            }
        }

        this.directories.push(...subDirs);
        return this.report;
    }

    async stop() {
        return this.report;
    }
}
