create database sq_tuqianyi DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use sq_tuqianyi;

create table user_t(
    user_id_c bigint,
    nick_c varchar(128),
    session_c varchar(255),
    last_login_c timestamp,
    primary key (nick_c)
);

create table notify_t(
	user_id_c bigint,
	lease_id_c bigint,
    nick_c varchar(128),
    validate_date_c datetime,
    invalidate_date_c datetime,
    fact_money_c double,
    subsc_type_c long,
    version_no_c integer,
    old_version_no_c integer,
    status_c long,
    gmt_create_date_c datetime,
    foreign key (user_id_c) references user_t(user_id_c) on delete cascade
);

create table category_t(
    category_id_c bigint,
    category_name_c varchar(128),
    primary key (category_id_c)
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

create table label_t(
    label_id_c bigint,
    category_id_c bigint,
    owner_c varchar(128),
    public_c smallint,
    storage_c smallint,
    url_c text,
    data_c blob,
    other_c blob,
    primary key (label_id_c),
    foreign key (owner_c) references user_t(nick_c) on delete cascade
);

create table text_area_t(
	text_area_id_c serial,
	label_id_c bigint,
	x_c integer,
	y_c integer,
	width_c integer,
	height_c integer,
	foreign key (label_id_c) references label_t(label_id_c) on delete cascade
);

create table merged_item_t(
    num_iid_c bigint,
    owner_c varchar(128),
    old_pic_url_c text,
    new_pic_url_c text,
    last_update_c datetime,
    time_limit_c bigint,
    action_c smallint,
    status_c smallint,
    error_code_c text,
    msg_c text,
    tag_c varchar(50),
    title_c varchar(128),
    price_c varchar(50),
    primary key (num_iid_c),
    foreign key (owner_c) references user_t(nick_c)
);

create table merge_t(
	merge_id_c bigint,
    item_num_iid_c bigint,
    label_id_c bigint,
    x_c integer,
    y_c integer,
    z_c integer,
    width_c integer,
    height_c integer,
    opacity_c integer,
    text_c varchar(10),
    other_c blob,
    foreign key (label_id_c) references label_t(label_id_c) on delete cascade,
    foreign key (item_num_iid_c) references merged_item_t(num_iid_c) on delete cascade
);

create table merge_text_t(
	text_id_c bigint,
	text_c varchar(50),
	font_c varchar(50),
	color_c char(6),
	back_color_c char(6),
	merge_id_c bigint,
	text_area_id_c bigint,
	foreign key (merge_id_c) references merge_t(merge_id_c),
	foreign key (text_area_id_c) references text_area_t
);

create table tag_t(
	tag_id_c serial,
	tag_c varchar(50),
	owner_c varchar(128),
	primary key (tag_id_c),
	foreign key (owner_c) references user_t(nick_c)
);

create table merge_tag_t(
	num_iid_c bigint,
	tag_id_c bigint,
	foreign key (num_iid_c) references merged_item_t(num_iid_c),
	foreign key (tag_id_c) references tag_t(tag_id_c)
);

create table properties_t(
	name_c varchar(128),
	value_c text,
	primary key (name_c)
);

insert into properties_t values('showNotice', 'false');

insert into category_t values(1, '热卖');
insert into category_t values(2, '新品');
insert into category_t values(3, '特价');
insert into category_t values(4, '打折');
insert into category_t values(5, '节日');
insert into category_t values(6, '其它');
insert into category_t values(7, '自定义');

create index idx_merged_item_owner on merged_item_t(owner_c);
create index idx_merged_item_status on merged_item_t(status_c);
create index idx_merged_item_error_code on merged_item_t(error_code_c);
