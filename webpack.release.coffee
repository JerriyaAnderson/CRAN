
path = require 'path'
webpack = require 'webpack'
ManifestPlugin = require 'webpack-manifest-plugin'
UglifyJSPlugin = require 'uglifyjs-webpack-plugin'
ExtractTextPlugin = require 'extract-text-webpack-plugin'

module.exports =
  entry:
    main: './entry/release'
    vendor: [
      './target/release/cljs.core'
      './target/release/respo.core'
      './target/release/respo_ui.style'
    ]
  output:
    path: path.join(__dirname, './dist/')
    filename: '[name].[chunkhash:8].js'
  devtool: 'source-map'
  module:
    rules: [
      test: /\.css$/
      loader: ExtractTextPlugin.extract
        fallback: 'style-loader'
        use: 'css-loader'
    ,
      test: /\.(eot|svg|ttf|woff2?)(\?.+)?$/
      loader: 'url-loader'
      query:
        limit: 100
        name: 'fonts/[hash:8].[ext]'
    ,
      test: /\.js$/
      loader: 'source-map-loader'
      options: { enforce: 'pre' }
    ]
  plugins: [
    new ExtractTextPlugin('[name].[chunkhash:8].css'),
    new webpack.optimize.CommonsChunkPlugin
      name: 'vendor',
      filename: 'vendor.[chunkhash:8].js'
    new UglifyJSPlugin sourceMap: true
    new ManifestPlugin
  ]
