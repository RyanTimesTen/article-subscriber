def load_sources
  YAML::load_file('sources.yml')[:sources]
end