const outputDir = "./target/scala-2.11/classes/"

module.exports = {
  entry: {
    admin: "./src/main/assets-admin/HakuperusteetAdminApp.jsx",
    spec: "./src/main/spec/HakuperusteetAdminSpec.js"
  },
  output: {
    path: outputDir,
    filename: "webapp-admin/js/[name].js",
    sourceMapFilename: "js/[name].map.json"
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