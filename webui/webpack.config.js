var path = require("path");

var minDir = "build/classes/main/min";

module.exports = {
    devtool: 'source-map',
    entry: path.resolve(__dirname, minDir, "webui.js"),
    output: {
        path: path.resolve(__dirname, "build/web"),
        filename: "robots.js"
    },
    resolve: {
        modules: [
            path.resolve(__dirname, "node_modules"),
            path.resolve(__dirname, minDir)
        ]
    }
};
