// The `db` variable is a default variable in the Mongo shell that
// typically refer to the current database. MongoDB `getSiblingDB()`
// method is used to select both existing databases or to create
// new ones. In this case, the script either selects the `kissen`
// database if it already exists or creates a new one with the same name.
// After this line `db` refers to the `kissen` database:
db = db.getSiblingDB('kissen');

// `createUser` is a MongoDB function used to create new users. It
// takes a document (object in JavaScript) as an argument, which includes
// the username, password, and roles that the user will be associated with.
// This function will create a user 'kissen' in the 'kissen' database,
// because `db` was assigned to the 'kissen' database on the previous line.
// The `readWrite` role allows the user all the read operations, and in
// addition, it allows update, insert and delete operations on:
// 1. all non-system collections
// 2. just the system.js collection
db.createUser(
    {
        // Specifies the name of the user account. In this case, it's "kissen":
        user: 'kissen',
        // Specifies the password of the user account. In a real-world scenario,
        // you'd want to encrypt the password or pull this from a secure environmental variable.
        pwd: 'kissen',
        roles: [
            {
                // The `readWrite` role is a built-in MongoDB role that allows
                // a user to read/write to the database. This gives user
                // 'kissen' the ability to perform read and write operations
                // on the 'kissen' database:
                role: 'readWrite',
                // Specifies the database to add the user to:
                db: 'kissen'
            }
        ]
    }
);
