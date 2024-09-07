package project.cs426.hospitalbulance.backend.database;

import java.util.List;

public class FirstAid {
	private String title, content;

	public FirstAid() {}

	public FirstAid(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return this.title;
	}

	public String getContent() {
		return this.content;
	}
}
