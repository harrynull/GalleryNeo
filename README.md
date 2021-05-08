# GalleryNeo

Your favorite good ol' Shopify Code Challenge now comes in Kotlin!

It implements the following features:
1. User registration, login with passwords securely hashed with [agon2](https://en.wikipedia.org/wiki/Argon2).
    1. comes with a session manager!
2. Upload images, handled securely (I know how vulnerable it could have been when doing CTF competitions).
3. Delete images, handled securely
4. Image permissions (private vs. public images).
5. bulk image upload/deletion
6. Elegant, efficient and easy-to-maintain code.

Written with Kotlin, Springboot, MySQL, protobuf

## Deployment/Development

You need to have an SQL database (tested with MySQL 8.0) installed and change the configuration in `application.properties`.
Alternatively, change your environment variables `MYSQL_HOST`, `MYSQL_USERNAME`, `MYSQL_PASSWORD`, `MYSQL_DB`.

Then, you can build it with `./gradlew build`

## License

This program is licensed under [AGPLv3](https://github.com/harrynull/GalleryNeo/blob/master/LICENSE).
That said, you are discouraged from using it for your own code challenge.
