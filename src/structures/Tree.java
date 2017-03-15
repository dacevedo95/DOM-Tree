package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	public void build() {
		Stack<TagNode> stack = new Stack<TagNode>();
		if(sc == null || !sc.hasNext()) {
			return;
		}
		
		sc.nextLine();
		root = new TagNode("html", null, null);
		stack.push(root);
		TagNode last = root;
		
		while(sc.hasNext()) {
			String tag = sc.nextLine();
			if(tag.startsWith("</")) {
				last = stack.peek();
				stack.pop();
			} else if(tag.startsWith("<")) {
				tag = tag.substring(1, tag.length() - 1);
				TagNode ptr = stack.peek().firstChild;
				if(ptr != null) {
					while(ptr.sibling != null) {
						ptr = ptr.sibling;
					}
					TagNode siblingNode = new TagNode(tag, null, null);
					ptr.sibling = siblingNode;
					stack.push(siblingNode);
					last = siblingNode;
				} else {				
					TagNode node1 = new TagNode(tag, null, null);
					last.firstChild = node1;
					last = node1;
					stack.push(node1);
				}
			} else {
				TagNode node2 = new TagNode(tag, null, null);
				if(last.firstChild != null) {
					last.sibling = node2;
				} else {
					last.firstChild = node2;
					last = node2;
				}
			}
		}
	}
	
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		preorderTraversal(root, oldTag, newTag);
	}
	
	private void preorderTraversal(TagNode root, String oldTag, String newTag) {
		if(root == null) {
			return; 
		}
		
		if(root.tag.equals(oldTag)) {
			root.tag = newTag;
		}
		
		preorderTraversal(root.firstChild, oldTag, newTag);
		preorderTraversal(root.sibling, oldTag, newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		boldRowRecursive(root, row, 0);
	}
	
	private void boldRowRecursive(TagNode root, int row, int count) {
		if(root == null) {
			return;
		}
		
		if(root.tag.equals("tr")) {
			count++;
		}
		
		if(count == row && root.tag.equals("td")) {
			TagNode boldTag = new TagNode("b", root.firstChild, null);
			root.firstChild = boldTag;
		}
		
		boldRowRecursive(root.firstChild, row, count);
		boldRowRecursive(root.sibling, row, count);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if (root == null) {
			return;
		} else { 
			while(hasTag(tag, root)) {
				removeRecursive(tag, root, root.firstChild);
			}
		}
	}

	private void removeRecursive(String tag, TagNode prevNode, TagNode currNode) {
		if (currNode == null || prevNode == null){
			return;
		} else if (currNode.tag.equals(tag)){
			if (tag.equals("ul") || tag.equals("ol"))
				changeListTag(currNode.firstChild); 
			if (prevNode.firstChild == currNode) {
				prevNode.firstChild = currNode.firstChild;
				TagNode curr = currNode.firstChild;
				TagNode sibling = currNode.sibling;
				while(curr.sibling != null) {
					curr = curr.sibling;
				}
				curr.sibling = sibling;
			} else if (prevNode.sibling == currNode) {
				TagNode curr = currNode.firstChild;
				TagNode sibling = currNode.sibling;
				while(curr.sibling != null) {
					curr = curr.sibling;
				}
				curr.sibling = sibling;
				prevNode.sibling = currNode.firstChild;
			}
			return;
		}
		prevNode = currNode;
		removeRecursive(tag, prevNode, currNode.firstChild);
		removeRecursive(tag, prevNode, currNode.sibling);
	}

	private void changeListTag(TagNode currNode) {
		if (currNode == null) {
			return;
		} else if (currNode.tag.compareTo("li") == 0) {
			currNode.tag = "p";
		}
		changeListTag(currNode.sibling);
	}

	private boolean hasTag(String tag, TagNode currNode) {
		if (currNode == null) {
			return false;
		} else if (currNode.tag.compareTo(tag) == 0) {
			return true;
		}
		return hasTag(tag, currNode.firstChild) || hasTag(tag, currNode.sibling);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		addPreTraverse(null, root, word, tag);
	}
	
	private void addPreTraverse(TagNode prev, TagNode curr, String word, String tag) {
		if (curr == null) {
			return;
		}
		if (tag.equals("html") || tag.equals("body") || tag.equals("p") || tag.equals("em") || tag.equals("b") || tag.equals("table") || tag.equals("tr") || tag.equals("td") || tag.equals("ol") || tag.equals("ul") || tag.equals("li")) {
			if(curr.tag.equals("html") || curr.tag.equals("body") || curr.tag.equals("p") || curr.tag.equals("em") || curr.tag.equals("b") || curr.tag.equals("table") || curr.tag.equals("tr") || curr.tag.equals("td") || curr.tag.equals("ol") || curr.tag.equals("ul") || curr.tag.equals("li")) {
				
			} else {
				String[] array = curr.tag.split(" ");
				int len = array.length;
				String bef = "";
				String tar = "";
				String aft = "";
				TagNode temp = new TagNode(tag, null, null);
				if (len == 1) {
					for (int i = 0; i < len; i++) {
						if (array[i].equalsIgnoreCase(word) || (word.equalsIgnoreCase(array[i].substring(0,  array[i].length() - 1)) && !Character.isLetter(array[i].charAt(array[i].length() - 1)))) {
							if (prev.firstChild == curr) {
								if (curr.sibling != null) {
									prev.firstChild = temp;
									temp.firstChild = curr;
									temp.sibling = curr.sibling;
									curr.sibling = null;
								}
								else {
									prev.firstChild = temp;
									temp.firstChild = curr;
								}
							}
							if (prev.sibling == curr) {
								if (curr.sibling != null) {
									prev.sibling = temp;
									temp.firstChild = curr;
									temp.sibling = curr.sibling;
									curr.sibling = null;
								}
								else {
									prev.sibling = temp;
									temp.firstChild = curr;
								}
							}
						}
					}	
				} else {
					TagNode head = null;
					TagNode tail = null;
					boolean bCheck = true, tCheck = true, aCheck = true;
					while (aCheck == true) {
						TagNode bTN = new TagNode(null, null, null), tTN = new TagNode(null, null, null), aTN = new TagNode(null, null, null);
						bef = "";
						tar = "";
						aft = "";
						for (int n = 0; n < len && (tCheck == true); n++) {
							if (array[n].equalsIgnoreCase(word) || (word.equalsIgnoreCase(array[n].substring(0,  array[n].length() - 1)) && !Character.isLetter(array[n].charAt(array[n].length() - 1)))) {
								bCheck = false;
								tCheck = false;
								tar = array[n];
								tTN.tag = tar;
								if (n != len - 1) {
								for (int m = n + 1; m < len; m++) {
									aft = aft + array[m] + " ";
								}
								aTN.tag = aft;
								}
							} else if (bCheck == true) {
								bef = bef + array[n] + " ";
								bTN.tag = bef;
							}
						}
						if (tCheck == true){
							if (prev.firstChild == curr)
								prev.firstChild = bTN;
							if (prev.sibling == curr)
								prev.sibling = bTN;
							break;
						}
						if (bTN.tag != null && tTN.tag != null && aTN.tag != null) {
							bTN.sibling = temp;
							temp.firstChild = tTN;
							temp.sibling = aTN;
						}
						else if (bTN.tag != null && tTN.tag != null) {
							bTN.sibling = temp;
							temp.firstChild = tTN;
							
						}
						else if (aTN.tag != null) {
							temp.firstChild = tTN;
							temp.sibling = aTN;
						}
						
						if (head == null && bTN.tag != null)
							head = bTN;
						else if (head == null && bTN.tag == null) {
							temp.firstChild = tTN;
							head = temp;
						}
						else
							tail = tTN;
						
						if (aTN.tag != null) {
							aCheck = true;
							array = aTN.tag.split(" ");
							len = array.length;
							if (head == null && bTN.tag != null)
								head = bTN;
							else if (head == null && bTN.tag == null) {
								temp.firstChild = tTN;
								head = temp;
							}
							else
								tail = aTN;
						}
						else
							aCheck = false;
					}
					if (prev.firstChild == curr)
						prev.firstChild = head;
					else if (prev.sibling == curr)
						prev.sibling = head;
					if(curr.sibling == null) {
						
					}
					else {
						tail.sibling = curr.sibling;
						curr.sibling = null;
					}
				}
			}
			addPreTraverse(curr, curr.firstChild, word, tag);
			addPreTraverse(curr, curr.sibling,  word, tag);
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
