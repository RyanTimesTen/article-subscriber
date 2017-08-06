require 'logger'
require 'nokogiri'
require 'open-uri'
require 'pg'
require 'yaml'

require_relative 'tooling/DBConnection'
require_relative 'tooling/helpers'
require_relative 'tooling/Parser'

RAKE_ROOT = File.expand_path(File.dirname(__FILE__))

$logger = Logger.new('log/logfile.log', 10, 1024000)
$logger.level = Logger::DEBUG
$logger.datetime_format = '%Y-%m-%d %H:%M:%S'
$logger.formatter = proc do |severity, datetime, progname, msg|
  "#{severity} -- #{datetime}: #{msg}\n"
end


Dir.glob("#{RAKE_ROOT}/tooling/rake/*").each { |r| import r }