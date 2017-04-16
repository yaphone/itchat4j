package cn.zhouyafeng.itchat4j;

public class AnotherSimpleTest {
	public static void main(String[] args) {
		User user = new User("123");
		System.out.println(user.getName());
		changeName(user);
		System.out.println(user.getName());

	}

	static void changeName(User user) {
		user.setName("yaphone");
	}
}

class User {
	public User(String name) {
		this.name = name;
	}

	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [name=" + name + "]";
	}
}