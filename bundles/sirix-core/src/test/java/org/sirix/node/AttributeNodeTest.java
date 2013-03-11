/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sirix.node;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sirix.Holder;
import org.sirix.TestHelper;
import org.sirix.api.PageReadTrx;
import org.sirix.exception.SirixException;
import org.sirix.node.delegates.NameNodeDelegate;
import org.sirix.node.delegates.NodeDelegate;
import org.sirix.node.delegates.ValNodeDelegate;
import org.sirix.utils.NamePageHash;

import com.google.common.base.Optional;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Attribute node test.
 */
public class AttributeNodeTest {

	/** {@link Holder} instance. */
	private Holder mHolder;
	
	/** Sirix {@link PageReadTrx} instance. */
	private PageReadTrx mPageReadTrx;
	
	@Before
	public void setUp() throws SirixException {
		TestHelper.closeEverything();
		TestHelper.deleteEverything();
		mHolder = Holder.generateDeweyIDSession();
		mPageReadTrx = mHolder.getSession().beginPageReadTrx();
	}

	@After
	public void tearDown() throws SirixException {
		mPageReadTrx.close();
		mHolder.close();
	}

	@Test
	public void testAttributeNode() {
		final byte[] value = { (byte) 17, (byte) 18 };

		final NodeDelegate del = new NodeDelegate(99, 13, 0, 0,
				Optional.of(SirixDeweyID.newRootID()));
		final NameNodeDelegate nameDel = new NameNodeDelegate(del, 13, 14, 15, 1);
		final ValNodeDelegate valDel = new ValNodeDelegate(del, value, false);

		final AttributeNode node = new AttributeNode(del, nameDel, valDel);

		// Create empty node.
		check(node);

		// Serialize and deserialize node.
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		node.getKind().serialize(out, node, null, mPageReadTrx);
		final ByteArrayDataInput in = ByteStreams.newDataInput(out.toByteArray());
		final AttributeNode node2 = (AttributeNode) Kind.ATTRIBUTE.deserialize(in, node.getNodeKey(), 
				mPageReadTrx);
		check(node2);
	}

	private final static void check(final AttributeNode node) {
		// Now compare.
		assertEquals(99L, node.getNodeKey());
		assertEquals(13L, node.getParentKey());

		assertEquals(13, node.getURIKey());
		assertEquals(14, node.getPrefixKey());
		assertEquals(15, node.getLocalNameKey());
	
		assertEquals(NamePageHash.generateHashForString("xs:untyped"),
				node.getTypeKey());
		assertEquals(2, node.getRawValue().length);
		assertEquals(Kind.ATTRIBUTE, node.getKind());
		assertEquals(true, node.hasParent());
		assertEquals(Kind.ATTRIBUTE, node.getKind());
		assertEquals(Optional.of(SirixDeweyID.newRootID()), node.getDeweyID());
	}

}
