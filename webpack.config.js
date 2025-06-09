const path = require('path');

module.exports = {
  entry: './src/main/js/index.js',
  output: {
    filename: 'custom-header.js',
    path: path.join(__dirname, 'src/main/webapp/js/bundles')
  },
  devtool: 'source-map',
  resolve: {
    extensions: [ '.js' ]
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader'
      }
    ]
  }
};