/*

BSA Application Schema

David Tompkins
9.20.2007

*/

-- Evolution Genetic/Neural Schema

drop table if exists evolutions;
create table evolutions (
  id           int          not null auto_increment,
  type         varchar(100) not null,
  description  varchar(100) not null,
  start_at     datetime     null,
  end_at       datetime     null,
  status       int          null,
  iteration    int          null,
  primary key (id)
);

drop table if exists phenotypes;
create table phenotypes (
  id                               int          not null auto_increment,
  type                             varchar(100) not null,
  description                      varchar(100) not null,
  created_at                       datetime     null,
  total_samples                    int          not null,
  total_correct                    int          not null,
  total_false_positive             int          not null,
  mean_class_error                 float        not null,
  mean_correct_probability         float        not null,
  mean_correct_variance            float        not null,
  mean_correct_pairwise_variance   float        not null,
  mean_incorrect_probability       float        not null,
  mean_incorrect_variance          float        not null,
  mean_incorrect_pairwise_variance float        not null,
  primary key (id)
);

drop table if exists generations;
create table generations (
  id           int          not null auto_increment,
  type         varchar(100) not null,
  description  varchar(100) not null,
  start_at     datetime     null,
  end_at       datetime     null,
  status       int          null,
  iteration    int          null,
  evolution_id int          null,
  phenotype_id int          null,
  constraint fk_generations_evolution foreign key (evolution_id) references evolutions(id),
  constraint fk_generations_phenotype foreign key (phenotype_id) references phenotypes(id),
  primary key (id)
);

drop table if exists networks;
create table networks (
  id             int          not null auto_increment,
  type           varchar(100) not null,
  xml            longtext     not null,
  created_at     datetime     null,
  phenotype_id   int          null default null,
  constraint fk_networks_phenotype foreign key (phenotype_id) references phenotypes(id),
  primary key (id)
);

-- Stock Sample Schema

drop table if exists load_reports;
create table load_reports (
  id         int       not null auto_increment,
  num_items  int       null,
  start_at   datetime  null,
  end_at     datetime  null,
  primary key (id)
);

drop table if exists tickers;
create table tickers (
  id    int         not null auto_increment,
  name  varchar(10) not null,
  primary key (id),
  index (name)
);

drop table if exists samples;
create table samples (
  id              int         not null auto_increment,
  ticker_id       int         not null,
  sample_date     date        not null,
  open_price      float       not null,
  high_price      float       not null,
  low_price       float       not null,
  close_price     float       not null,
  volume          bigint      not null,
  adj_close_price float       not null,
  created_at      datetime    null,
  updated_at      datetime    null,
  primary key (id),
  constraint fk_samples_ticker foreign key (ticker_id) references tickers(id),
  unique (ticker_id,sample_date),
  index (ticker_id,sample_date),
  index (ticker_id)
);

drop table if exists users;
create table users (
  id              int           not null auto_increment,
  name            varchar(100)  not null,
  hashed_password char(40)      null,
  primary key (id)
);

lock tables load_reports write;
insert into load_reports values(
'1',                    #id 
'1',                    #num_items
'2007-05-24 06:00:00',  #start_at
'2007-05-24 06:01:00'); #end_at
unlock tables;

lock tables users write;
insert into users values(
null,                                         # id 
'test21',                                     # name
'ded1b9400958eed750245018efc12698047ad7e7');  # hashed password
unlock tables;

lock tables tickers write;
insert into tickers values(
'1',     #id 
'TEST'); #name
unlock tables;

lock tables samples write;

insert into samples values(
"DEFAULT",                 #id 
'1',                       #ticker_id
'1970-01-01',              #sample_date
'1.0',                     #open_price
'1.0',                     #high_price
'1.0',                     #low_price
'1.0',                     #close_price
'1',                       #volume
'1.0',                     #adj_close_price
'2007-05-06 06:00:00',     #created_at
'2007-05-06 06:00:00');    #updated_at

insert into samples values(
"DEFAULT",                 #id 
'1',                       #ticker_id
'1970-01-02',              #sample_date
'1.0',                     #open_price
'1.0',                     #high_price
'1.0',                     #low_price
'1.0',                     #close_price
'1',                       #volume
'1.1',                     #adj_close_price
'2007-05-06 06:00:00',     #created_at
'2007-05-06 06:00:00');    #updated_at

insert into samples values(
"DEFAULT",                 #id 
'1',                       #ticker_id
'1970-01-03',              #sample_date
'1.0',                     #open_price
'1.0',                     #high_price
'1.0',                     #low_price
'1.0',                     #close_price
'1',                       #volume
'1.0',                     #adj_close_price
'2007-05-06 06:00:00',     #created_at
'2007-05-06 06:00:00');    #updated_at

insert into samples values(
"DEFAULT",                 #id 
'1',                       #ticker_id
'1970-01-04',              #sample_date
'1.0',                     #open_price
'1.0',                     #high_price
'1.0',                     #low_price
'1.0',                     #close_price
'1',                       #volume
'1.1',                     #adj_close_price
'2007-05-06 06:00:00',     #created_at
'2007-05-06 06:00:00');    #updated_at

insert into samples values(
"DEFAULT",                 #id 
'1',                       #ticker_id
'1970-01-05',              #sample_date
'1.0',                     #open_price
'1.0',                     #high_price
'1.0',                     #low_price
'1.0',                     #close_price
'1',                       #volume
'1.0',                     #adj_close_price
'2007-05-06 06:00:00',     #created_at
'2007-05-06 06:00:00');    #updated_at

insert into samples values(
"DEFAULT",                 #id 
'1',                       #ticker_id
'1970-01-06',              #sample_date
'1.0',                     #open_price
'1.0',                     #high_price
'1.0',                     #low_price
'1.0',                     #close_price
'1',                       #volume
'1.1',                     #adj_close_price
'2007-05-06 06:00:00',     #created_at
'2007-05-06 06:00:00');    #updated_at

unlock tables;
