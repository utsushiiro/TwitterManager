USE twitter_manager;

create table profile_img_hash(
  userId INTEGER NOT NULL,
  image_hash_code VARCHAR(40)
);