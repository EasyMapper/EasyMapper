# EasyMapper

EasyMapper is an easy-to-use object mapping library for Java, designed to simplify the process of mapping values between models representing objects in a domain. This is especially helpful when dealing with models that have many properties and deep object graphs. EasyMapper automates the object mapping process, allowing you to focus more on your business logic.

Here is a concise example demonstrating how EasyMapper simplifies property mapping between two objects:

```java
@Getter
@Setter
public class User {
    private long id;
    private String email;
    private String passwordHash;
}
```

```java
public record UserView(long id, String email) {
}
```

```java
public class UserViewGenerator {

    public UserView generateView(User entity) {
        return new Mapper().map(entity, UserView.class);
    }
}
```

In this example, EasyMapper effortlessly manages property mapping from a `User` object to a `UserView` object, guaranteeing a smooth data transfer process. With EasyMapper, you can reap the advantages of effortless object mapping, irrespective of whether your object is structured as a class or a record.

## Requirements

- JDK 1.8 or higher

## Installation

### Maven

To include EasyMapper in your Maven project, add the following dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>io.github.easymapper</groupId>
  <artifactId>easymapper</artifactId>
  <version>0.3.1</version>
</dependency>
```

### Gradle

If you are using Gradle, include EasyMapper by adding the following line to your `build.gradle` file:

```groovy
implementation 'io.github.easymapper:easymapper:0.3.1'
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

Mapping properties between `User` and `UserView` becomes a breeze with EasyMapper.
To employ EasyMapper for mapping a class, it is essential to apply the `@java.beans.ConstructorProperties` annotation to the constructor of the target class. If you are utilizing Lombok, you can streamline this process by adding the setting `lombok.anyConstructor.addConstructorProperties = true` to your lombok.config file. This configuration instructs Lombok to automatically include the `@java.beans.ConstructorProperties` annotation in the constructors it generates.

```text
lombok.anyConstructor.addConstructorProperties = true
```

If you are not utilizing Lombok, you will have to manually include the `@java.beans.ConstructorProperties` annotation to your constructors. However, if you opt for a record instead of a class, the `@java.beans.ConstructorProperties` annotation is not necessary.

With this configuration in place, you can easily convert an entity to a view object using the `Mapper` class:

```java
package account.presentation;

import account.domainmodel.User;
import easymapper.Mapper;

public final class UserViewGenerator {

    private final Mapper mapper = new Mapper();

    public UserView generateView(User entity) {
        return mapper.map(entity, UserView.class);
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

### Property Mapping Customization

EasyMapper allows you to customize the mapping of properties between objects by providing a `MappingConfiguration` object to the `Mapper` class. This configuration object allows you to specify how properties should be mapped between objects.

Here is an example of how you can customize the mapping of properties between two objects:

```java
package ordermanagement.domainmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private UUID id;
    private long itemId;
    private int quantity;
}
```

```java
package ordermanagement.presentation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderView {
    private UUID orderId;
    private long itemId;
    private int numberOfItems;
}
```

```java
package ordermanagement.presentation;

import ordermanagement.domainmodel.Order;
import easymapper.Mapper;

public class OrderViewGenerator {

    private static Mapper mapper = new Mapper(config -> config
        .addMapping(Order.class, OrderView.class, mapping -> mapping
            .set("orderId", Order::getId)
            .set("numberOfItems", Order::getQuantity)));

    public OrderView generateView(Order entity) {
        return mapper.map(entity, OrderView.class);
    }
}
```

In the given example, the mapping APIs enable you to specify custom mapping rules. Here, we've customized the mapping for the `id` property to be mapped to `orderId` and the `quantity` property to be mapped to `numberOfItems` when transferring data from the `Order` object to the `OrderView` object. This flexibility enables you to fine-tune the mapping process to precisely meet your requirements.

### Type to Type Transformation

In the context of type-to-type transformation, EasyMapper excels in handling scenarios where you need to convert one custom data type into another seamlessly. Consider the following example:

```java
package listing.domainmodel;

public record Pricing(int listPrice, int discount) {
}
```

```java
package listing.presentation;

public record PricingView(int listPrice, int discount, int salePrice) {
}
```

```java
package listing.presentation;

import listing.domainmodel.Pricing;
import easymapper.Mapper;

public class PricingViewGenerator {

    private static Mapper mapper = new Mapper(config -> config
        .addTransform(Pricing.class, PricingView.class, x -> new PricingView(
            x.getListPrice(),
            x.getDiscount(),
            x.getListPrice() - x.getDiscount())));

    public PricingView generateView(Pricing entity) {
        return mapper.map(entity, PricingView.class);
    }
}
```

In this example, EasyMapper facilitates the transformation of a `Pricing` object, which contains `listPrice` and `discount` properties, into a `PricingView` object. The transformation is carried out using the `addTransform` method, allowing you to specify custom logic for mapping the properties. In this case, a new `PricingView` object is created, including the calculated `salePrice`, which is the result of subtracting the discount from the list price.

EasyMapper's ability to handle such type-to-type transformations simplifies the process of mapping between different custom data types, ensuring your data flows seamlessly through your application layers.

## easymapper-kotlin

EasyMapper also provides a Kotlin extension library, `easymapper-kotlin`, which offers a more concise syntax for mapping properties between objects.

### Install

#### Maven

To include EasyMapper Kotlin in your Maven project, add the following dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>io.github.easymapper</groupId>
  <artifactId>easymapper-kotlin</artifactId>
  <version>0.3.1</version>
</dependency>
```

#### Gradle

If you are using Gradle, include EasyMapper Kotlin by adding the following line to your `build.gradle` file:

```groovy
implementation 'io.github.easymapper:easymapper-kotlin:0.3.1'
```

### `KotlinMapper` Class

The `KotlinMapper` class is specifically designed to streamline the object mapping process for Kotlin classes. Below is an example illustrating its usage:

```kotlin
package account.domainmodel

class User(val id: Long, val username: String, val passwordHash: String)
```

```kotlin
package account.presentation

class UserView(val id: Long, val username: String)
```

```kotlin
package account.presentation

import account.domainmodel.User
import easymapper.kotlin.KotlinMapper

class UserViewGenerator {

    private val mapper: KotlinMapper = KotlinMapper()

    fun generateView(entity: User): UserView {
        return mapper.map(entity)
    }
}
```

In this Kotlin-centric example, the `KotlinMapper` simplifies the mapping process. It automatically maps properties from a `User` object to a `UserView` object without the need for explicit mapping configurations. The `generateView` function demonstrates how effortless it is to generate a `UserView` instance from a `User` instance using the `KotlinMapper`.

In Kotlin, you don't need to decorate constructors with the `@java.beans.ConstructorProperties` annotation when using `KotlinMapper`.

When employing `KotlinMapper`, you have the flexibility to customize it just like the standard `Mapper` class. Let's delve into a more detailed example:

```kotlin
package listing.domainmodel

data class Pricing(val listPrice: Int, val discount: Int)
```

```kotlin
package listing.presentation

data class PricingView(val listPrice: Int, val discount: Int, val salePrice: Int)
```

```kotlin
package listing.presentation

import listing.domainmodel.Pricing

class PricingViewGenerator {

    private val mapper: KotlinMapper = KotlinMapper { config -> config
        .addMapping<Pricing, PricingView> { mapping -> mapping
            .set(PricingView::salePrice) { it.listPrice - it.discount }
        }
    }

    fun generateView(entity: Pricing): PricingView {
        return mapper.map(entity)
    }
}
```
In this extended example, we demonstrate how to leverage `KotlinMapper` for a more customized mapping experience. Similar to the standard `Mapper`, you can define explicit mapping configurations. Here, we've set up a mapping rule for the `salePrice` property in the `PricingView` class to be calculated based on the `listPrice` and `discount` properties from the `Pricing` object.

With `KotlinMapper`, you can seamlessly tailor your mapping logic to match your specific requirements, making it a powerful tool for mapping Kotlin data classes efficiently.
