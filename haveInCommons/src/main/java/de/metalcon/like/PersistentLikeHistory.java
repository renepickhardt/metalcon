package de.metalcon.like;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicInteger;

import de.metalcon.like.Like.Vote;
import de.metalcon.utils.MUIDConverter;

/**
 * @author Jonas Kunze
 * 
 */
public class PersistentLikeHistory {
	/*
	 * timestamp(4 B), UUID(8 B), flag(1 B)
	 */
	private static final int BytesPerLikeInFile = 13;

	/*
	 * Path to the folder where all files will be stored
	 */
	private static String storageDir;

	/*
	 * How many chars of the MUID should be used to define the relative path.
	 * The MUID abcdefghijklm will be stored in following folder for this
	 * variable set to 3:
	 * 
	 * sotragedir/m/l/k/abcdefghijklm_likes
	 */
	private static final int storageRecursiveDepth = 3;

	/*
	 * The maximum number of open file handles allowed.
	 */
	private static final int MaximumOpenFileHandles = 10000;

	/*
	 * The number of currently opened file handles
	 */
	private static final AtomicInteger NumberOfOpenFileHandles = new AtomicInteger(
			0);

	/*
	 * Path to the file storing the likes
	 */
	private final String fileName;

	/*
	 * Number of bytes in the key file
	 */
	private int fileSize = 0;

	/*
	 * The first long of each files is the length of the file in bytes
	 */
	private RandomAccessFile RAFile = null;
	private MappedByteBuffer file = null;
	private long fileBufferSize = 0;

	/**
	 * 
	 * @param fileName
	 *            Path to the file storing the requested Map. If the file
	 *            doesn't exist, a new Instance will be initialized and returned
	 * @throws IOException
	 * 
	 */
	public PersistentLikeHistory(final long uuid) {
		this.fileName = generateFileName(uuid);
		openFile();
		fileSize = file.getInt();
		if (fileSize == 0) {
			fileSize = 4;
		}
		closeFileIfNecessary();
	}

	/**
	 * Create all folders that might be used
	 * 
	 * @throws IOException
	 */
	public static void initialize(final String storageDir) throws IOException {
		PersistentLikeHistory.storageDir = storageDir;

		char[] counters = new char[storageRecursiveDepth];
		char[] availableFolderNames = MUIDConverter.getAllowedFolderNames();

		while (true) {
			String relPath = "";
			for (int i = 0; i < counters.length; i++) {
				relPath += availableFolderNames[counters[i]] + "/";
			}
			final File dir = new File(storageDir, relPath);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new IOException("Unable to create "
						+ dir.getAbsolutePath());
			}
			int j = 0;
			while (++counters[j] == availableFolderNames.length) {
				if (j == counters.length - 1) {
					return;
				}
				counters[j++] = 0;
			}
		}
	}

	private static String generateFileName(final long uuid) {

		return storageDir + "/" + MUIDConverter.getMUIDStoragePath(uuid) + uuid
				+ "_likes";
	}

	/**
	 * Opens the file handle if it was closed
	 * 
	 * @throws FileNotFoundException
	 */
	private void openFilesIfClosed() {
		if (file == null) {
			openFile();
		}
	}

	/**
	 * Creates the memory mapped files
	 */
	private void openFile() {
		try {
			NumberOfOpenFileHandles.addAndGet(1);
			RAFile = new RandomAccessFile(fileName, "rw");
			updateMemoryMappedBuffer(RAFile.length());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void updateMemoryMappedBuffer(final long minimumBufferSize) {
		try {
			if (minimumBufferSize < 4096)
				fileBufferSize = 4096;
			else
				fileBufferSize = minimumBufferSize;

			file = RAFile.getChannel().map(MapMode.READ_WRITE, 0,
					fileBufferSize);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Close the file handle. It will be automatically reopened as soon as a
	 * disk access is needed.
	 * 
	 * @return true if the handles were opened and we were able to close it
	 */
	public boolean closeFile() {
		try {
			if (file != null) {
				NumberOfOpenFileHandles.decrementAndGet();
				RAFile.close();
				file = null;
				RAFile = null;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return false;
	}

	/**
	 * Close the file handle if too many are opened. It will be automatically
	 * reopened as soon as a disk access is needed.
	 */
	public void closeFileIfNecessary() {
		if (NumberOfOpenFileHandles.get() > MaximumOpenFileHandles) {
			closeFile();
		}
	}

	/**
	 * Seeks the persistent likes file and returns an array of all like found
	 * with the timestamp TS being startTs< TS < stopTS
	 * 
	 * 
	 * @param startTS
	 *            All returned likes will have a higher timestamp
	 * @param stopTS
	 *            All returned likes will have a lower timestamp
	 * @return The array of all found likes or <code>null</code> if no like was
	 *         found or the file was empty. The elements are ordered by
	 *         descending timestamps (newest first).
	 */
	Like[] getLikesWithinTimePeriod(final int startTS, final int stopTS) {
		final int totalLines = (fileSize - 4) / BytesPerLikeInFile;
		if (totalLines == 0) {
			return null;
		}

		openFilesIfClosed();

		/*
		 * TODO: What if the first searched like is at the last line? Check if
		 * it will be found
		 */
		int left = 0, currentLine = totalLines, right = totalLines;
		int currentTs;
		while (left != right) {
			// Goto middle of left and right
			currentLine = left + ((right - left) / 2);

			currentTs = file.getInt(currentLine * BytesPerLikeInFile + 4);
			if (currentTs > startTS) {
				right = currentLine;
			} else {
				if (left == currentLine) {
					/*
					 * As currentTs <= searchedTS we need the element one to the
					 * right
					 */
					currentLine = right;
					break;
				}
				left = currentLine;
			}
		}

		if (currentLine == totalLines) {
			closeFileIfNecessary();
			return null; // Nothing found: All likes are older than startTS
		}

		Like[] result = new Like[(int) (totalLines - currentLine)];
		int resultPointer = result.length - 1;

		/*
		 * Move to currentLine
		 */
		file.position(currentLine * BytesPerLikeInFile + 4);

		long uuid;
		byte flag;

		ByteBuffer buffer;
		byte[] linebuffer = new byte[BytesPerLikeInFile];

		for (long line = currentLine; line < totalLines; line++) {
			/*
			 * Read the whole line at once and use a ByteBuffer to access the
			 * different fields (as in memcpy)
			 */
			file.get(linebuffer, 0, BytesPerLikeInFile);
			buffer = ByteBuffer.wrap(linebuffer);
			currentTs = buffer.getInt();

			if (currentTs > stopTS) {
				break;
			}
			uuid = buffer.getLong();
			flag = buffer.get();
			result[resultPointer--] = new Like(uuid, currentTs,
					Vote.getByFlag(flag));
		}

		closeFileIfNecessary();

		if (resultPointer != -1) {
			Like[] tmp = new Like[result.length - resultPointer - 1];
			System.arraycopy(result, resultPointer + 1, tmp, 0, tmp.length);
			return tmp;
		}
		return result;
	}

	/**
	 * Writes the new like to the cache and the persistent file
	 * 
	 * @param like
	 *            The new like performed by this entity
	 * @return true in case of success, false if a problem with the FS occurred
	 */
	void addLike(Like like) {

		openFilesIfClosed();

		if (fileBufferSize <= fileSize + BytesPerLikeInFile) {
			updateMemoryMappedBuffer(fileSize + BytesPerLikeInFile);
		}

		/*
		 * Append the new like to the end of the persistent file
		 */
		file.position(fileSize);
		file.putInt(like.getTimestamp());
		file.putLong(like.getUUID());
		file.put(like.getVote().value);
		fileSize += BytesPerLikeInFile;

		file.putInt(0, fileSize);

		// raf.writeInt(like.getTimestamp());
		// raf.writeLong(like.getUUID());
		// raf.writeByte(like.getVote().value);

		closeFileIfNecessary();
	}

	public void delete() {
		File file = new File(fileName);
		file.renameTo(new File(fileName + ".deleted"));
	}
}
