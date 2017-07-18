namespace :scrape do

  desc 'Test scraper'
  task :source, [:source] do |t, args|

    $logger.info('Running `rake scrape:source`')
    if args[:source].nil?
      $logger.fatal('Parameter `source` missing. Exiting.')
      exit
    end

    begin
      source_name = args[:source]
      $logger.info("Using source: #{source_name}")

      scrape source_name
    rescue => e
      $logger.error("An error occurred: #{e}")
    ensure
      $logger.close
    end

  end

  def scrape source_name
    begin
      source = { :name => source_name, :base_url => parse_for_url(source_name) }
      source[:articles] = Parser.parse_html source
      db = DbConnection.new
      db.insert source
      db.disconnect
    rescue
    end
  end

  def parse_for_url source_name
    $logger.info('Attempting to load sources.yml')
    begin
      sources = load_sources
      sources[source_name.to_sym][:base]
    rescue => e
      raise e
    end
  end

end