package org.mcraft.kantanmemory.core.data;

/**
 * 
 * @author Henry Hu
 *
 */
public class Word implements Comparable<Word> {
	private String name;
	private String hint;
	private String translation;
	private int accent;

	// For generating YAML file
	protected Word() {

	}

	public Word(String name, String hint, String translation) {
		this.name = name;
		this.hint = hint;
		this.translation = translation;
		this.accent = -1;
	}

	public Word(String name, String kana, String translation, int accent) {
		this.name = name;
		this.hint = kana;
		this.translation = translation;
		this.accent = accent;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Word) {
			return (this.name.equals(((Word) o).getName())) && (this.hint.equals(((Word) o).getHint()))
					&& (this.translation.equals(((Word) o).getTranslation()));
		}
		return false;
	}

	@Override
	public int compareTo(Word anotherWord) {
		// TODO Auto-generated method stub
		if (this.equals(anotherWord)) {
			return 0;
		} else if (this.hint.compareTo(anotherWord.getHint()) != 0) {
			return this.hint.compareTo(anotherWord.getHint());
		} else if (this.name.compareTo(anotherWord.getName()) != 0) {
			return this.name.compareTo(anotherWord.getName());
		} else if (this.translation.compareTo(anotherWord.getTranslation()) != 0) {
			return this.translation.compareTo(anotherWord.getTranslation());
		}
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String kana) {
		this.hint = kana;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public int getAccent() {
		return accent;
	}

	public void setAccent(int accent) {
		this.accent = accent;
	}

}
