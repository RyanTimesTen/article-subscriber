class Parser

  @@selectors = {
    :time_tech => {
      :xpath => '//a[@target="_self"]',
      :css => 'a.text'
    } 
  }

  def self.parse_html source
    $logger.info("Attempting to parse html for source: #{source[:name]}")
    begin
      source_xpath = @@selectors[source[:name].to_sym][:xpath]
      source_css = @@selectors[source[:name].to_sym][:css]

      Nokogiri::HTML(open(source[:base_url])).xpath(source_xpath).css(source_css).map do |link|
        { :name => link.text, :link => link['href'] }
      end
    rescue => e
      $logger.error("Error when parsing html for source: #{source[:name]}: #{e}")
    end
  end

end