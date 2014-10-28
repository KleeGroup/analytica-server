package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HKey;
import io.vertigo.lang.Assertion;

import java.util.ArrayList;
import java.util.List;

final class AppQueue {
	static final class QueueItem {
		final HKey key;
		final HCube cube;

		QueueItem(final HKey key, final HCube cube) {
			Assertion.checkNotNull(key);
			Assertion.checkNotNull(cube);
			//---------------------------------------------------------------------
			this.key = key;
			this.cube = cube;
		}
	}

	private final List<QueueItem> queue = new ArrayList<>();

	public synchronized void push(final QueueItem queueItem) {
		Assertion.checkNotNull(queueItem);
		//---------------------------------------------------------------------
		queue.add(queueItem);
	}

	synchronized int size() {
		return queue.size();
	}

	synchronized QueueItem pop() {
		if (queue.size() == 0) {
			return null;
		}
		return queue.remove(queue.size() - 1);
	}
}
