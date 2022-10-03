# Japper

Japper is an easy-to-use object mapping library for Java.

Mapping values between models that represent one object in a domain is painful especially when the model has many properties and the graph of the object is deep. Japper provides automated object mapping so you can focus more on your business logic.

## Requirements

- JDK 1.8 or higher

## Install

### Maven

```xml
<dependency>
  <groupId>io.github.cleanpojo</groupId>
  <artifactId>japper</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.cleanpojo:japper:0.1.0'
```

## Features

### Object Projection

When defining the domain model and presentation model to represent users,

```java
/*
 * /User/me/myapp/src/main/java/account/domainmodel/User.java
 */

package account.domainmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class User {

    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String middleName;
    private String lastName;
}
```

```java
/*
 * /User/me/myapp/src/main/java/account/presentation/UserView.java
 */

package account.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class UserView {

    private long id;
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
}
```
you didn't just want to expose password information to the presentation layer, but you had to go through the following efforts.

```java
package account.presentation;

import account.domainmodel.User;

public final class UserViewGenerator {

    public UserView generateView(User entity) {
        return new Userview(
            entity.getId(),
            entity.getEmail(),
            entity.getUsername(),
            entity.getFirstName(),
            entity.getMiddleName(),
            entity.getLastName()
        );
    }
}
```

And don't forget that you made a bug.

It is very easy to map between `User` and `UserView` using Japper.

Add `lombok.anyConstructor.addConstructorProperties = true` setting to `lombok.config` file so that Lombok add a `@java.beans.ConstructorProperties` to generated constructors.

```text
# /User/me/myapp/lombok.config

lombok.anyConstructor.addConstructorProperties = true
```

If you are not using Lombok, you can put a `@java.beans.ConstructorProperties` to constructors yourself instead.

Now you can easily convert User to UserView using Japper.

```java
package account.presentation;

import account.domainmodel.User;
import japper.Mapper;

public final class UserViewGenerator {

    public UserView generateView(User entity) {
        return new Mapper().map(entity, UserView.class);
    }
}
```

### Property Projection

`Mapper` provides the method to project properties of a source object to an existing destination object.

```java
/*
 * /User/me/myapp/src/main/java/account/persistencemodel/UserEntity.java
 */

package account.persistencemodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public final class UserEntity {

    @Id
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    private String firstName;

    private String middleName;

    private String lastName;

    @Version
    private Integer version; // For optimistic concurrency control. Not related to the domain model.
}
```

```java
/*
 * /User/me/myapp/src/main/java/account/persistencemodel/UserRepository.java
 */

package account.persistencemodel;

import account.domainmodel.User;
import japper.Mapper;

public final class UserRepository {

    private final Mapper mapper = new Mapper();

    void update(User domainModel) {
        UserEntity persistenceModel = findById(domainModel.getId());
        mapper.map(domainModel, persistenceModel); // Map properties of 'domainModel' to 'persistenceModel'.
        repository.save(dataModel);
    }
}
```
