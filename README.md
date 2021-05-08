# GalleryNeo

Your favorite good ol' Shopify Code Challenge now comes in Kotlin!

It implements the following features:

1. User registration, login with passwords securely hashed with [argon2id](https://en.wikipedia.org/wiki/Argon2).
    1. comes with a session manager!
2. Upload images, handled securely (I know how vulnerable it could have been when doing CTF competitions).
3. Delete images, handled securely
4. Image permissions (private vs. public images).
5. Elegant, efficient and
   easy-to-maintain [code](https://github.com/harrynull/GalleryNeo/blob/master/src/main/kotlin/tech/harrynull/galleryneo/)
   .
6. Comprehensive [tests](https://github.com/harrynull/GalleryNeo/blob/master/src/test/kotlin/tech/harrynull/galleryneo/)
7. All written in one day (and no I didn't pull an all nighter for this)!

What wasn't done because of time constraints (But I could have done it if needed):

1. Cache with Redis.
2. Admin permissions.
3. Pagination
4. Checking if the uploaded file has proper image header (so users don't abuse it as a cloud drive)
5. Persisting uploaded file to remote locations (e.g. Amazon S3)
6. A beautiful frontend.

Written with Kotlin, Springboot, MySQL, protobuf

## Deployment/Development

You need to have an SQL database (tested with MySQL 8.0) installed and change the configuration
in `application.properties`. Alternatively, change your environment variables `MYSQL_HOST`, `MYSQL_USERNAME`
, `MYSQL_PASSWORD`, `MYSQL_DB`. The default values are `localhost:3306/gallery` and `localhost:3306/gallery_test` with
username and password as root

Then, you can run it with `./gradlew bootRun` and it will start a server at `127.0.0.1:8080`

You can also run the tests with `./gradlew test`

If you somehow really wants to use it in production, it is recommended to put it behind nginx and use the compiled jar instead.

## License

This program is licensed under [AGPLv3](https://github.com/harrynull/GalleryNeo/blob/master/LICENSE). That said, you are
discouraged from using it for your own code challenge.
