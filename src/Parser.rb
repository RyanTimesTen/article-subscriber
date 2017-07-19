class Parser

  def time_tech source
    $logger.info("Attempting to parse html for source: #{source[:name]}")
    begin
      Nokogiri::HTML(open(source[:base_url])).xpath('//a[@target="_self"]').css('a.text').map do |link|
        { :name => link.text, :link => link['href'] }
      end
    rescue => e
      $logger.error("Error when parsing html for source: #{source[:name]}: #{e}")
    end
  end

  def facebook_blog source
    $logger.info("Attempting to parse html for source: #{source[:name]}")
    begin
      Nokogiri::HTML(open('https://code.facebook.com')).xpath('//a').map do |link|
        { :name => link.text, :link => link['href'] } if link['href'] =~ Regexp.new('posts/[0-9]')
      end.keep_if { |link| !link.nil? }
    rescue => e
      $logger.error("Error when parsing html for source: #{source[:name]}: #{e}")
    end
  end

end