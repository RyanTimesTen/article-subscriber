class Parser

  def self.parse source
    $logger.info("Attempting to parse html for source: #{source[:name]}")
    begin
      Nokogiri::HTML(open(source[:base_url])).xpath(source[:xpath]).map do |link|
        { :name => link.text, :link => link['href'] }
      end
    rescue => e
      $logger.error("Error when parsing html for source: #{source[:name]}: #{e}")
    end
  end

end