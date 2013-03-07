#!/opt/local/bin/ruby

require 'typhoeus'
require 'logger'
require 'optparse'
require 'yaml'
require 'active_record'
require 'csv'
    
$logger = Logger.new(STDOUT)
$logger.level = Logger::INFO

class Sample < ActiveRecord::Base

  belongs_to :ticker
  
  protected

  def self.all_samples
    find(:all)
  end

  def self.find_or_create(ticker_name, date, open_price, high_price, low_price, close_price, volume, adj_close_price)

    # Date,Open,High,Low,Close,Volume,Adj Close

    ticker = Ticker.find_or_create(ticker_name)

    sample = Sample.find(:first, :conditions => [ "sample_date = ? and ticker_id = ?", date, ticker.id ])
    $logger.info "find_or_create:sample found, id: #{sample.id}" unless sample.nil?
    return sample unless sample.nil?

    sample = Sample.create(
      :ticker => ticker,
      :sample_date => date,
      :open_price => open_price,
      :high_price => high_price,
      :low_price => low_price,
      :close_price => close_price,
      :volume => volume,
      :adj_close_price => adj_close_price)
    $logger.info "find_or_create:sample created, id: #{sample.id}"
    return sample

  end

  def self.find_most_recent(ticker_name)

    ticker = Ticker.find(:first, :conditions => [ "name = ?", ticker_name ])
    sample = ticker.samples.find(:first, :order => "sample_date DESC")
    $logger.info "find_most_recent:sample found, date: #{sample.sample_date}"
    return sample
  end
end

class Ticker < ActiveRecord::Base

  has_many :samples
  validates_uniqueness_of :name

  protected
  
  def self.find_or_create(name)

    name = name.capitalize

    $logger.info("Ticker:find_or_create: #{name}")

    ticker = Ticker.find(:first, :conditions => [ "name = ?", name ])

    if !ticker.nil? then
      $logger.info("Ticker:find_or_create: found #{ticker.name}, id=#{ticker.id}")
      return ticker
    end

    ticker = Ticker.create(:name => name)
    $logger.info("Ticker:find_or_create: created #{ticker.name}, id=#{ticker.id}")
    return ticker

  end

end

class SampleUpdater

  DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20120427 Firefox/15.0a1"
  DEFAULT_ACCEPT = "*/*"
  DEFAULT_HOST = "ichart.finance.yahoo.com"
  DEFAULT_CONNECTION = "Keep-Alive"
  DEFAULT_START_DATE = "1970-01-01"
  SAMPLE_QUERY_URL = "http://ichart.finance.yahoo.com/table.csv?s=TICKER&a=START_MONTH&b=START_DAY&c=START_YEAR&d=END_MONTH&e=END_DAY&f=END_YEAR&g=d&ignore=.csv"

  @@activerecord = nil

  def initialize(ticker)
    @ticker = ticker
    @start_date = DEFAULT_START_DATE
    init_activerecord
  end

  def logger
    $logger
  end

  def set_start_date(start_date)
    @start_date = start_date
  end

  def update
    logger.info "-------------------------"
    logger.info "starting update for #{@ticker}, start date #{@start_date}"
    logger.info "-------------------------"

    query = SAMPLE_QUERY_URL.sub("TICKER", @ticker)
    start_year, start_month, start_day = parse_date(@start_date)
    query = query.sub("START_YEAR", start_year.to_i.to_s)
    query = query.sub("START_MONTH", (start_month.to_i-1).to_s) # Y! counts months from 0
    query = query.sub("START_DAY", start_day.to_i.to_s)
    now = Time.new
    query = query.sub("END_YEAR", now.year.to_s)
    query = query.sub("END_MONTH", (now.month-1).to_s)
    query = query.sub("END_DAY", now.day.to_s)
    logger.info "update query URL is: #{query}"
    samples_csv = get_http_content(query)
    rows = CSV.parse(samples_csv, :headers => true, :header_converters => :symbol, :converters => :numeric)
    logger.info "found #{rows.length} samples"
    
    count = 0
    begin
      (rows.length-1).downto(0) do |i|
        row = rows[i]
        count += 1
        # Date,Open,High,Low,Close,Volume,Adj Close
        date = row[:date]
        open_price = row[:open]
        high_price = row[:high]
        low_price = row[:low]
        close_price = row[:close]
        volume = row[:volume]
        adj_close_price = row[:adj_close]
        logger.info "sample:#{@ticker}: #{date}, #{open_price}, #{high_price}, #{low_price}, #{close_price}, #{volume}, #{adj_close_price}"
        sample = Sample.find_or_create(@ticker, date, open_price, high_price, low_price, close_price, volume, adj_close_price)
      end
    rescue Exception => exc
      logger.error("Error:update:#{@ticker}:#{exc.message}")
    end
    logger.info "added #{count} samples for #{@ticker}"
  end

  protected

  def init_activerecord
    @activerecord = ActiveRecord::Base.establish_connection( :adapter => "mysql", :host => "localhost", :username => "bsa_user", :database => "bsa_development", :password => "bsa210077") unless !@activerecord.nil?
  end

  def parse_date(date) # YYYY-MM-DD
    fields = date.split("-")
    return nil if fields.length != 3 or fields[0].length != 4 or fields[1].length != 2 or fields[2].length != 2
    return fields[0], fields[1], fields[2]
  end
  
  def get_http_content(requested_url)
    logger.info "HTTP Get: #{requested_url}"
    response = Typhoeus::Request.get(requested_url)
    if response.code != 200 then
      logger.warn "error: response code not 200 (#{response.code})"
      return nil
    end
    return response.body
  end 

end

options = Hash.new
optparse = OptionParser.new do|opts|
  
  opts.banner = "Usage: update_samples.rb [options]"
 
  options[:verbose] = false
  opts.on( '-v', '--verbose', 'Enable debug logging' ) do
    options[:verbose] = true
  end
 
  options[:ticker] = nil
  opts.on( '-t', '--ticker TICKER', 'ticker' ) do |ticker|
    options[:ticker] = ticker
  end
 
  options[:start_date] = nil
  opts.on( '-s', '--start-date DATE', 'Start date, e.g. 2005-01-01' ) do |date|
    options[:start_date] = date
  end
 
  opts.on( '-h', '--help', 'Display this screen' ) do
    puts opts
    exit
  end
end
optparse.parse!

if options[:ticker].nil? then
  # default to usage summary
  puts optparse.banner
  optparse.summarize(STDOUT)
  exit
end

su = SampleUpdater.new(options[:ticker])
$logger.level = Logger::WARN unless options[:verbose]
su.set_start_date(options[:start_date]) unless options[:start_date].nil?
su.update
