package apptest.dchan;

public class Contact {
	private String mEmailAddress;
	private String mName;

	public Contact(String _name, String _email) {
		mName = _name;
		mEmailAddress = _email;
	}

	public String getEmailAddress() {
		return mEmailAddress;
	}

	public void setEmail(String emailAddress) {
		this.mEmailAddress = emailAddress;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}
}
