/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.library;

import java.util.Collections;

import org.geometerplus.fbreader.book.*;

public class AuthorTree extends FilteredTree {
	public final Author Author;

	AuthorTree(IBookCollection collection, Author author) {
		super(collection, new Filter.ByAuthor(author));
		Author = author;
	}

	AuthorTree(AuthorListTree parent, Author author, int position) {
		super(parent, new Filter.ByAuthor(author), position);
		Author = author;
	}

	@Override
	public String getName() {
		return Author.NULL.equals(Author)
			? resource().getResource("unknownAuthor").getValue() : Author.DisplayName;
	}

	@Override
	protected String getStringId() {
		return "@AuthorTree" + getSortKey();
	}

	@Override
	protected String getSortKey() {
		if (Author.NULL.equals(Author)) {
			return null;
		}
		return new StringBuilder()
			.append(" Author:")
			.append(Author.SortKey)
			.append(":")
			.append(Author.DisplayName)
			.toString();
	}

	private SeriesTree getSeriesSubtree(Series series) {
		final SeriesTree temp = new SeriesTree(Collection, series, Author);
		int position = Collections.binarySearch(subtrees(), temp);
		if (position >= 0) {
			return (SeriesTree)subtrees().get(position);
		} else {
			return new SeriesTree(this, series, Author, - position - 1);
		}
	}

	@Override
	protected boolean createSubtree(Book book) {
		final SeriesInfo seriesInfo = book.getSeriesInfo();
		if (seriesInfo != null) {
			return getSeriesSubtree(seriesInfo.Series).createSubtree(book);
		}

		final BookTree temp = new BookTree(Collection, book);
		int position = Collections.binarySearch(subtrees(), temp);
		if (position >= 0) {
			return false;
		} else {
			new BookTree(this, book, - position - 1);
			return true;
		}
	}
}
