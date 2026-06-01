#!/usr/bin/env node

import { getFSReport } from '../lib/FSStatLib.js';
import path from 'path';

function parseArguments() {
  const args = process.argv.slice(2);

  if (args.length < 3) {
    console.error("fsstat -- file system statistics\n")
    console.error('Usage: fsstat <directory> <maxFileSize> <numBands>');
    console.error('  directory    - Path to scan (absolute or relative)');
    console.error('  maxFileSize  - Maximum file size for band distribution (in bytes)');
    console.error('  numBands     - Number of size bands to create');
    console.error('\nExample: node App.js /home 1000000 10');
    process.exit(2);
  }

  const [dirArg, maxFSArg, numBandsArg] = args;

  const directory = path.resolve(dirArg);

  const maxFileSize = parseInt(maxFSArg, 10);
  if (isNaN(maxFileSize) || maxFileSize <= 0) {
    console.error('Error: maxFileSize must be a positive integer');
    process.exit(2);
  }

  const numBands = parseInt(numBandsArg, 10);
  if (isNaN(numBands) || numBands <= 0) {
    console.error('Error: numBands must be a positive integer');
    process.exit(2);
  }

  return { directory, maxFileSize, numBands };
}

function formatBytes(bytes) {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function displayReport(report, directory, maxFileSize, numBands) {
  console.log('\n' + '='.repeat(60));
  console.log('FSStatLib Report');
  console.log('='.repeat(60));
  console.log(`Directory: ${directory}`);
  console.log(`Total Files: ${report.totalFiles}`);
  console.log(`Max File Size Threshold: ${formatBytes(maxFileSize)}`);
  console.log('\nFile Size Distribution:');
  console.log('-'.repeat(60));

  const bandSize = maxFileSize / numBands;
  for (let i = 0; i < numBands; i++) {
    const rangeStart = formatBytes(i * bandSize);
    const rangeEnd = formatBytes((i + 1) * bandSize);
    const count = report.sizeDistribution[i];
    console.log(`Band ${i + 1}: [${rangeStart}, ${rangeEnd}) - ${count} files`);
  }

  const overflowCount = report.sizeDistribution[numBands];
  console.log(`Above Max: [${formatBytes(maxFileSize)}, ∞) - ${overflowCount} files`);

  console.log('='.repeat(60) + '\n');
}

async function main() {
  try {
    const { directory, maxFileSize, numBands } = parseArguments();

    console.log(`\nScanning ${directory}...`);
    const report = await getFSReport(directory, maxFileSize, numBands);

    displayReport(report, directory, maxFileSize, numBands);
  } catch (error) {
    console.error('Error:', error.message);
    process.exit(1);
  }
}

main();
