/* automatically generated by JSCoverage - do not edit */
if (typeof _$jscoverage === 'undefined') _$jscoverage = {};
if (! _$jscoverage['appenders/file.js']) {
  _$jscoverage['appenders/file.js'] = [];
  _$jscoverage['appenders/file.js'][1] = 0;
  _$jscoverage['appenders/file.js'][2] = 0;
  _$jscoverage['appenders/file.js'][11] = 0;
  _$jscoverage['appenders/file.js'][12] = 0;
  _$jscoverage['appenders/file.js'][13] = 0;
  _$jscoverage['appenders/file.js'][28] = 0;
  _$jscoverage['appenders/file.js'][29] = 0;
  _$jscoverage['appenders/file.js'][30] = 0;
  _$jscoverage['appenders/file.js'][31] = 0;
  _$jscoverage['appenders/file.js'][32] = 0;
  _$jscoverage['appenders/file.js'][34] = 0;
  _$jscoverage['appenders/file.js'][36] = 0;
  _$jscoverage['appenders/file.js'][37] = 0;
  _$jscoverage['appenders/file.js'][38] = 0;
  _$jscoverage['appenders/file.js'][39] = 0;
  _$jscoverage['appenders/file.js'][45] = 0;
  _$jscoverage['appenders/file.js'][52] = 0;
  _$jscoverage['appenders/file.js'][53] = 0;
  _$jscoverage['appenders/file.js'][55] = 0;
  _$jscoverage['appenders/file.js'][58] = 0;
  _$jscoverage['appenders/file.js'][61] = 0;
  _$jscoverage['appenders/file.js'][63] = 0;
  _$jscoverage['appenders/file.js'][64] = 0;
  _$jscoverage['appenders/file.js'][68] = 0;
  _$jscoverage['appenders/file.js'][69] = 0;
  _$jscoverage['appenders/file.js'][70] = 0;
  _$jscoverage['appenders/file.js'][71] = 0;
  _$jscoverage['appenders/file.js'][74] = 0;
  _$jscoverage['appenders/file.js'][75] = 0;
  _$jscoverage['appenders/file.js'][78] = 0;
  _$jscoverage['appenders/file.js'][81] = 0;
  _$jscoverage['appenders/file.js'][82] = 0;
}
_$jscoverage['appenders/file.js'][1]++;
"use strict";
_$jscoverage['appenders/file.js'][2]++;
var layouts = require("../layouts"), path = require("path"), fs = require("fs"), streams = require("../streams"), os = require("os"), eol = os.EOL || "\n", openFiles = [];
_$jscoverage['appenders/file.js'][11]++;
process.on("exit", (function () {
  _$jscoverage['appenders/file.js'][12]++;
  openFiles.forEach((function (file) {
  _$jscoverage['appenders/file.js'][13]++;
  file.end();
}));
}));
_$jscoverage['appenders/file.js'][28]++;
function fileAppender(file, layout, logSize, numBackups) {
  _$jscoverage['appenders/file.js'][29]++;
  var bytesWritten = 0;
  _$jscoverage['appenders/file.js'][30]++;
  file = path.normalize(file);
  _$jscoverage['appenders/file.js'][31]++;
  layout = layout || layouts.basicLayout;
  _$jscoverage['appenders/file.js'][32]++;
  numBackups = numBackups === undefined? 5: numBackups;
  _$jscoverage['appenders/file.js'][34]++;
  numBackups = numBackups === 0? 1: numBackups;
  _$jscoverage['appenders/file.js'][36]++;
  function openTheStream(file, fileSize, numFiles) {
    _$jscoverage['appenders/file.js'][37]++;
    var stream;
    _$jscoverage['appenders/file.js'][38]++;
    if (fileSize) {
      _$jscoverage['appenders/file.js'][39]++;
      stream = new streams.RollingFileStream(file, fileSize, numFiles);
    }
    else {
      _$jscoverage['appenders/file.js'][45]++;
      stream = fs.createWriteStream(file, {encoding: "utf8", mode: parseInt("0644", 8), flags: "a"});
    }
    _$jscoverage['appenders/file.js'][52]++;
    stream.on("error", (function (err) {
  _$jscoverage['appenders/file.js'][53]++;
  console.error("log4js.fileAppender - Writing to file %s, error happened ", file, err);
}));
    _$jscoverage['appenders/file.js'][55]++;
    return stream;
}
  _$jscoverage['appenders/file.js'][58]++;
  var logFile = openTheStream(file, logSize, numBackups);
  _$jscoverage['appenders/file.js'][61]++;
  openFiles.push(logFile);
  _$jscoverage['appenders/file.js'][63]++;
  return (function (loggingEvent) {
  _$jscoverage['appenders/file.js'][64]++;
  logFile.write(layout(loggingEvent) + eol, "utf8");
});
}
_$jscoverage['appenders/file.js'][68]++;
function configure(config, options) {
  _$jscoverage['appenders/file.js'][69]++;
  var layout;
  _$jscoverage['appenders/file.js'][70]++;
  if (config.layout) {
    _$jscoverage['appenders/file.js'][71]++;
    layout = layouts.layout(config.layout.type, config.layout);
  }
  _$jscoverage['appenders/file.js'][74]++;
  if (options && options.cwd && ! config.absolute) {
    _$jscoverage['appenders/file.js'][75]++;
    config.filename = path.join(options.cwd, config.filename);
  }
  _$jscoverage['appenders/file.js'][78]++;
  return fileAppender(config.filename, layout, config.maxLogSize, config.backups);
}
_$jscoverage['appenders/file.js'][81]++;
exports.appender = fileAppender;
_$jscoverage['appenders/file.js'][82]++;
exports.configure = configure;
_$jscoverage['appenders/file.js'].source = ["\"use strict\";","var layouts = require('../layouts')",", path = require('path')",", fs = require('fs')",", streams = require('../streams')",", os = require('os')",", eol = os.EOL || '\\n'",", openFiles = [];","","//close open files on process exit.","process.on('exit', function() {","  openFiles.forEach(function (file) {","    file.end();","  });","});","","/**"," * File Appender writing the logs to a text file. Supports rolling of logs by size."," *"," * @param file file log messages will be written to"," * @param layout a function that takes a logevent and returns a string "," *   (defaults to basicLayout)."," * @param logSize - the maximum size (in bytes) for a log file, "," *   if not provided then logs won't be rotated."," * @param numBackups - the number of log files to keep after logSize "," *   has been reached (default 5)"," */","function fileAppender (file, layout, logSize, numBackups) {","  var bytesWritten = 0;","  file = path.normalize(file);","  layout = layout || layouts.basicLayout;","  numBackups = numBackups === undefined ? 5 : numBackups;","  //there has to be at least one backup if logSize has been specified","  numBackups = numBackups === 0 ? 1 : numBackups;","","  function openTheStream(file, fileSize, numFiles) {","    var stream;","    if (fileSize) {","      stream = new streams.RollingFileStream(","        file,","        fileSize,","        numFiles","      );","    } else {","      stream = fs.createWriteStream(","        file, ","        { encoding: \"utf8\", ","          mode: parseInt('0644', 8), ","          flags: 'a' }","      );","    }","    stream.on(\"error\", function (err) {","      console.error(\"log4js.fileAppender - Writing to file %s, error happened \", file, err);","    });","    return stream;","  }","","  var logFile = openTheStream(file, logSize, numBackups);","  ","  // push file to the stack of open handlers","  openFiles.push(logFile);","  ","  return function(loggingEvent) {","    logFile.write(layout(loggingEvent) + eol, \"utf8\");","  };","}","","function configure(config, options) {","  var layout;","  if (config.layout) {","    layout = layouts.layout(config.layout.type, config.layout);","  }","","  if (options &amp;&amp; options.cwd &amp;&amp; !config.absolute) {","    config.filename = path.join(options.cwd, config.filename);","  }","","  return fileAppender(config.filename, layout, config.maxLogSize, config.backups);","}","","exports.appender = fileAppender;","exports.configure = configure;"];
