package gr.aueb.mscis.softeng.team6.delivery.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

/**
 * Client entity.
 *
 * @since 0.1.0
 */
@NamedQueries({
  @NamedQuery(
      name = "findClientByUsername",
      query = "from Client where username like :username",
      readOnly = true,
      fetchSize = 1)
})
@Entity
@Table(name = "client")
public class Client implements Serializable {
  /** Auto-generated ID field. */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Username field. */
  @NotNull
  @NaturalId
  @Length(min = 1, max = 20)
  @Pattern(regexp = "[a-zA-Z0-9_-]+")
  @Column(length = 20, unique = true)
  private String username;

  /** Real name field. */
  @NotNull @NotBlank private String name;

  /** Password field. */
  @Embedded @Valid private Password password;

  /** Email address field. */
  @Embedded @Valid private EmailAddress email;

  /** Phone number field. */
  @Embedded @Valid private PhoneNumber phone;

  /** Address field. */
  @Embedded @Valid private Address address;

  /** Orders relation field. */
  @OneToMany(mappedBy = "client")
  private List<Order> orders;

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public Client setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getName() {
    return name;
  }

  public Client setName(String name) {
    this.name = name;
    return this;
  }

  public Password getPassword() {
    return password;
  }

  public Client setPassword(Password password) {
    this.password = password;
    return this;
  }

  public EmailAddress getEmail() {
    return email;
  }

  public Client setEmail(EmailAddress email) {
    this.email = email;
    return this;
  }

  public PhoneNumber getPhone() {
    return phone;
  }

  public Client setPhone(PhoneNumber phone) {
    this.phone = phone;
    return this;
  }

  public Address getAddress() {
    return address;
  }

  public Client setAddress(Address address) {
    this.address = address;
    return this;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public Client setOrders(List<Order> orders) {
    this.orders = orders;
    return this;
  }

  @Override
  public boolean equals(Object that) {
    return this == that || (that instanceof Client other && username.equals(other.username));
  }

  @Override
  public int hashCode() {
    return username.hashCode();
  }

  @Override
  public String toString() {
    return String.format(
        "Client{username=\"%s\", name=\"%s\", email=\"%s\", phone=\"%s\"}",
        username, name, email, phone);
  }
}
