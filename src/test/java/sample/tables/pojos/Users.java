/*
 * This file is generated by jOOQ.
*/
package sample.tables.pojos;


import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import javax.annotation.Generated;
import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@Generated(
        value = {
                "http://www.jooq.org",
                "jOOQ version:3.9.1"
        },
        comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Users implements Serializable, Persistable<Integer> {

    private static final long serialVersionUID = 430179770;
    @Id
    private Integer id;
    private String name;
    private String email;

    public Users() {
    }

    public Users(Users value) {
        this.id = value.id;
        this.name = value.name;
        this.email = value.email;
    }

    public Users(
            Integer id,
            String name,
            String email
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Integer getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Users (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(email);

        sb.append(")");
        return sb.toString();
    }
}
