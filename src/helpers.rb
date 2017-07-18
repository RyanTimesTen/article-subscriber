def load_sources
  begin
    YAML::load_file('sources.yml')[:sources]
  rescue
    @logger.error('Unable to open sources.yml')
    raise IOError
  end
end