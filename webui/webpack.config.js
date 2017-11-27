var path = require("path");

var kotlinDceOutputDir = "build/classes/main/min";

module.exports = {
    devtool: 'source-map',
    entry: path.resolve(__dirname, kotlinDceOutputDir, "webui.js"),
    output: {
        path: path.resolve(__dirname, "build/web"),
        filename: "robots.js"
    },
    resolve: {
        modules: [
            path.resolve(__dirname, "node_modules"),
            path.resolve(__dirname, kotlinDceOutputDir)
        ]
    },
    module: {
        rules: [
            { test: /\.js/, use: 'source-map-loader'}
        ]
    }
};
