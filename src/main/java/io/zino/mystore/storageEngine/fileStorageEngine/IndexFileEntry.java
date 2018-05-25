package io.zino.mystore.storageEngine.fileStorageEngine;

public class IndexFileEntry {

	private long child1;
	private long child2;
	private long child3;
	private long child4;
	private long child5;
	
	private long value;
	private String key;
	
	
	public long getChild1() {
		return child1;
	}
	public void setChild1(long child1) {
		this.child1 = child1;
	}
	public long getChild2() {
		return child2;
	}
	public void setChild2(long child2) {
		this.child2 = child2;
	}
	public long getChild3() {
		return child3;
	}
	public void setChild3(long child3) {
		this.child3 = child3;
	}
	public long getChild4() {
		return child4;
	}
	public void setChild4(long child4) {
		this.child4 = child4;
	}
	public long getChild5() {
		return child5;
	}
	public void setChild5(long child5) {
		this.child5 = child5;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public IndexFileEntry(long child1, long child2, long child3, long child4, long child5, long value, String key) {
		super();
		this.child1 = child1;
		this.child2 = child2;
		this.child3 = child3;
		this.child4 = child4;
		this.child5 = child5;
		this.value = value;
		this.key = key;
	}
	
	public long getChildAt(int at){
		if(at<=0 || at>5) return -1;
		switch (at) {
		case 1:
			return this.getChild1();
		case 2:
			return this.getChild2();
		case 3:
			return this.getChild3();
		case 4:
			return this.getChild4();
		case 5:
			return this.getChild5();
		default:
			return -1;
		}
	}
	
	public void setChildAt(int at, long value){
		if(at<=0 || at>5) return;
		switch (at) {
		case 1:
			this.setChild1(value);
			break;
		case 2:
			this.setChild2(value);
			break;
		case 3:
			this.setChild3(value);
			break;
		case 4:
			this.setChild4(value);
			break;
		case 5:
			this.setChild5(value);
			break;
		}
	}
	
}
