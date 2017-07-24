class DbConnection

  def initialize
    $logger.info('Attempting to connect to database')
    begin
      @connection = PG.connect :dbname => 'article-subscriber', :user => ENV['PG_USER'], :password => ENV['PG_PASS']
    rescue => e
      $logger.error("Failed to connect to database: #{e}")
      @connection.close if @connection
    end
  end

  def disconnect
    if @connection
      $logger.info('Closing database connection')
      @connection.close
    end
  end

  def insert source
    @name = source[:name]
    @article_base_url = source[:article_base_url]
    @articles = source[:articles]

    $logger.info("Checking if table exists: #{@name}")
    create_table if !table_exists

    _insert
  end

  private

  def table_exists
    result = @connection.exec "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename='#{@name}');"
    result.first['exists'] == 't' ? true : false
  end

  def create_table
    $logger.info("Creating table #{@name}")
    @connection.exec "CREATE TABLE \"#{@name}\" (name text primary key, link text);"
  end

  def _insert
    @articles.each do |article|
      begin
        full_url = @article_base_url + article[:link]
        $logger.info("Inserting #{article[:name]} and #{full_url} into #{@name}")
        @connection.exec "INSERT INTO \"#{@name}\" VALUES ('#{article[:name].gsub("'", %q(''))}', '#{full_url}');"
      rescue => e
        $logger.error("An error occurred: #{e}")
      end
    end
  end

end