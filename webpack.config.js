const outputDir = "./target/scala-2.11/classes/"

module.exports = {
  entry: {
    "webapp/js/app": "./src/main/assets/HakuperusteetApp.jsx",
    "webapp/js/spec": "./src/main/spec/HakuperusteetSpec.js",
    "webapp-admin/js/admin": "./src/main/assets-admin/HakuperusteetAdminApp.jsx",
    "webapp-admin/js/spec": "./src/main/spec/HakuperusteetAdminSpec.js"
  },
  output: {
    path: outputDir,
    filename: "[name].js",
    sourceMapFilename: "[name].map.json"
  },
  module: {
    loaders: [
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        loader: 'babel'
      },
      {
        test: /\.less$/,
        loader: "style!css!less"
      },
      {
        test: /\.png$/,
        loader: "url-loader",
        query: { mimetype: "image/png" }
      },
      {
        include: /\.json$/,
        loaders: ["json-loader"]
      }
    ]
  }
}