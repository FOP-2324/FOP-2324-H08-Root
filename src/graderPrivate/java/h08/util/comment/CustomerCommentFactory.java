package h08.util.comment;

import h08.Customer;

@SuppressWarnings("unused")
public class CustomerCommentFactory extends CommentFactory<Customer> {

    private boolean firstName;
    private boolean lastName;
    private boolean address;
    private boolean dateOfBirth;

    @Override
    public String build(Customer customer) {

        StringBuilder builder = new StringBuilder("Customer{");

        boolean first = true;

        if (firstName) {
            builder.append("firstName='").append(customer.firstName()).append('\'');
            first = false;
        }
        if (lastName) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("lastName='").append(customer.lastName()).append('\'');
            first = false;
        }
        if (address) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("address='").append(customer.address()).append('\'');
            first = false;
        }
        if (dateOfBirth) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("dateOfBirth=").append(customer.dateOfBirth());
        }

        builder.append('}');

        return builder.toString();
    }

    public CustomerCommentFactory firstName() {
        this.firstName = true;
        return this;
    }

    public CustomerCommentFactory lastName() {
        this.lastName = true;
        return this;
    }

    public CustomerCommentFactory address() {
        this.address = true;
        return this;
    }

    public CustomerCommentFactory dateOfBirth() {
        this.dateOfBirth = true;
        return this;
    }

}
