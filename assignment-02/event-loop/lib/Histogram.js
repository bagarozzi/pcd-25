
// An histogram is a data structure that counts the number of file for each
// size band. 
export class Histogram {
    #numBands;
    #maxFileSize;
    #bands;
    #directoriesScanned = 0;

    constructor(numBands, maxFileSize) {
        this.#numBands = numBands;
        this.#maxFileSize = maxFileSize;
        this.#bands = new Array(numBands + 1).fill(0);
    }

    update(fileSize) {
        let band = Math.floor(fileSize * this.#numBands / this.#maxFileSize);
        if (band > this.#numBands) {
            this.#bands[this.#numBands]++;
        }
        else {
            this.#bands[band]++;
        }
    }

    updateDirectories() {
        this.#directoriesScanned++;
    }

    getScannedDirectories() {
        return this.#directoriesScanned;
    }

    getDistribution() {
        let dist = new Map();
        for(let i = 0; i < this.#numBands + 1; i++) {
            let start = i * this.#maxFileSize / this.#numBands;
            let end = (i == this.#numBands) ? Infinity : (i + 1) * this.#maxFileSize / this.#numBands;
            dist.set([start, end], this.#bands[i]);
        }
        return dist;
    }

    getTotalFiles() {
        return this.#bands.reduce((a, b) => a + b, 0);
    }

}