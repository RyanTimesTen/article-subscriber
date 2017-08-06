require 'logger'
require 'nokogiri'
require 'open-uri'
require 'pg'
require 'yaml'

$logger = Logger.new('log/logfile.log', 10, 1024000)
$logger.level = Logger::DEBUG
$logger.datetime_format = '%Y-%m-%d %H:%M:%S'
$logger.formatter = proc do |severity, datetime, progname, msg|
  "#{severity} -- #{datetime}: #{msg}\n"
end
#
RAKE_ROOT = File.expand_path(File.dirname(__FILE__))

Dir.glob("#{RAKE_ROOT}/tooling/*.rb").each { |r| require_relative r }
Dir.glob("#{RAKE_ROOT}/tooling/rake/*").each { |r| import r }