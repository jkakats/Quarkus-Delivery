package gr.aueb.mscis.softeng.team6.delivery.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

/**
 * Client entity.
 *
 * @since 0.1.0
 * @version 0.1.1
 */
@NamedQueries({
  @NamedQuery(
      name = "findFrequentClients",
      query =
          """
        select c from Client c join c.orders o
          where o.store = :store
          and (o.orderedTime between :start and :end)
        group by c
        order by count(o) desc
        """,
      readOnly = true)
})
@Entity
@Table(
    name = "client",
    indexes = {@Index(columnList = "address_zip_code")})
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
