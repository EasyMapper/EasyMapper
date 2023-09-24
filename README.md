# EasyMapper

EasyMapper is an easy-to-use object mapping library for Java, designed to simplify the process of mapping values between models representing objects in a domain. This is especially helpful when dealing with models that have many properties and deep object graphs. EasyMapper automates the object mapping process, allowing you to focus more on your business logic.

## Requirements

- JDK 1.8 or higher

## Installation

### Maven

To include EasyMapper in your Maven project, add the following dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>io.github.easymapper</groupId>
  <artifactId>easymapper</artifactId>
  <version>0.1.1</version>
</dependency>
```

### Gradle

If you are using Gradle, include EasyMapper by adding the following line to your `build.gradle` file:

```groovy
implementation 'io.github.easymapper:easymapper:0.1.1'
```

## Features

### Object Projection

Suppose you have domain and presentation models to represent users as follows:

```java
package account.domainmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class User {
    private final long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String firstName;
    private final String middleName;
    private final String lastName;
}
```

```java
package account.presentation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class UserView {
    private final long id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String middleName;
    private final String lastName;
}
```

If your goal is to expose user information to the presentation layer while avoiding the exposure of sensitive data such as the password, you might initially create a UserViewGenerator class as shown below:

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

However, it's crucial to note that there's a bug in the code above.

Mapping properties between `User` and `UserView` becomes a breeze with EasyMapper. To enhance the process further, you can add the `lombok.anyConstructor.addConstructorProperties = true` setting to your `lombok.config` file, allowing Lombok to automatically add a `@java.beans.ConstructorProperties` annotation to the generated constructors. If you're not using Lombok, you can manually add the `@java.beans.ConstructorProperties` annotation to the constructors yourself.

```text
lombok.anyConstructor.addConstructorProperties = true
```

With this configuration in place, you can easily convert an entity to a view object using the `Mapper` class:

```java
package account.presentation;

import account.domainmodel.User;
import easymapper.Mapper;

public final class UserViewGenerator {

    public UserView generateView(User entity) {
        return new Mapper().map(entity, UserView.class);
    }
}
```

Now, mapping properties between `User` and `UserView` is simplified and more efficient, thanks to EasyMapper and the added Lombok configuration.

### Property Projection

The `Mapper` class offers a method for projecting properties from a source object to an existing destination object. This can be particularly useful when you need to update properties of an existing object based on another object.

Let's consider an example using `UserEntity` and `User` classes:

```java
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

    // For optimistic concurrency control. Not related to the domain model.
    @Version
    private Integer version;
}
```

```java
package account.persistencemodel;

import account.domainmodel.User;
import easymapper.Mapper;
import java.util.function.Consumer;

public final class UserRepository {

    private final Mapper mapper = new Mapper();

    void update(long id, Consumer<User> updater) {
        // Find the entity by id.
        UserEntity persistenceModel = findById(id);
        
        // Map properties of 'persistenceModel' to 'domainModel'.
        User domainModel = mapper.map(persistenceModel, User.class);

        // Update properties of 'domainModel'.
        updater.accept(domainModel);

        // Map properties of 'domainModel' to 'persistenceModel'.
        mapper.map(domainModel, persistenceModel);

        // Save the entity.
        repository.save(dataModel);
    }
}
```

In this example, the `update` method in the `UserRepository` class updates a `UserEntity` based on a provided `Consumer` function that modifies a `User` domain model. Here's how it works:

1. It retrieves the `UserEntity` from the database based on the provided `id`.
1. The `Mapper` class is used to map properties from `UserEntity` to a `User` domain model.
1. The provided `updater` function is invoked to update properties on the `User` domain model.
1. The `Mapper` class is again used to project properties from the updated `User` domain model back to the original `UserEntity`.
1. Finally, the updated `UserEntity` is saved back to the repository.

This demonstrates how EasyMapper can simplify property projection when working with objects in different layers of an application.
