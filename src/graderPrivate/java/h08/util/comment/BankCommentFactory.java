package h08.util.comment;

import h08.Bank;

@SuppressWarnings("unused")
public class BankCommentFactory extends CommentFactory<Bank> {

    private boolean name;
    private boolean bic;
    private boolean capacity;
    private boolean size;

    @Override
    public String build(Bank bank) {

        StringBuilder builder = new StringBuilder("Bank{");
        boolean first = true;

        if (name) {
            builder.append("name='").append(bank.getName()).append('\'');
            first = false;
        }
        if (bic) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("bic='").append(bank.getBic()).append('\'');
            first = false;
        }
        if (capacity) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("capacity=").append(bank.capacity());
            first = false;
        }
        if (size) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("size=").append(bank.size());
        }

        builder.append('}');

        return builder.toString();
    }

    public h08.util.comment.BankCommentFactory name() {
        this.name = true;
        return this;
    }

    public h08.util.comment.BankCommentFactory bic() {
        this.bic = true;
        return this;
    }

    public h08.util.comment.BankCommentFactory capacity() {
        this.capacity = true;
        return this;
    }

    public h08.util.comment.BankCommentFactory size() {
        this.size = true;
        return this;
    }

}
