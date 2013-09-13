package de.metalcon.musicStorageServer;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Test;
import org.xiph.libogg.ogg_packet;
import org.xiph.libogg.ogg_page;
import org.xiph.libogg.ogg_stream_state;
import org.xiph.libvorbis.vorbis_block;
import org.xiph.libvorbis.vorbis_comment;
import org.xiph.libvorbis.vorbis_dsp_state;
import org.xiph.libvorbis.vorbis_info;
import org.xiph.libvorbis.vorbisenc;

public class OggVorbisEncodingTest {

	private static final File sourceFile = new File(
			"/home/sebschlicht/test.mp3");

	private static final File tmpFile = new File("/tmp/test.wav");

	private static final File destinationFile = new File(
			"/home/sebschlicht/test.ogg");

	private static final int NUM_BYTES = 1024;

	@Test
	public void testResampling() throws IOException,
			UnsupportedAudioFileException {
		final AudioInputStream in = AudioSystem
				.getAudioInputStream(new BufferedInputStream(
						new FileInputStream(sourceFile), 1024));
		final AudioInputStream pcm = AudioSystem.getAudioInputStream(
				AudioFormat.Encoding.PCM_SIGNED, in);
		final AudioInputStream ulaw = AudioSystem.getAudioInputStream(
				AudioFormat.Encoding.ULAW, pcm);
		AudioSystem.write(ulaw, AudioFileFormat.Type.WAVE, tmpFile);

		final vorbis_info vorbisInfo = new vorbis_info();
		final vorbisenc vorbisEncoder = new vorbisenc();
		final vorbis_dsp_state vorbisState = new vorbis_dsp_state();

		if (!vorbisEncoder.vorbis_encode_init(vorbisInfo, 2, 44100, -1, 128000,
				-1)) {
			fail("Failed to initialize vorbis encoder!");
		}

		if (!vorbisState.vorbis_analysis_init(vorbisInfo)) {
			fail("Failed to initialize vorbis DSP state!");
		}

		final vorbis_comment vorbisComment = new vorbis_comment();
		final vorbis_block vorbisBlock = new vorbis_block(vorbisState);

		// create header
		final ogg_stream_state oggStreamState = new ogg_stream_state(
				new Random().nextInt(256));
		final ogg_packet header = new ogg_packet();
		final ogg_packet header_comm = new ogg_packet();
		final ogg_packet header_code = new ogg_packet();
		vorbisState.vorbis_analysis_headerout(vorbisComment, header,
				header_comm, header_code);

		oggStreamState.ogg_stream_packetin(header);
		oggStreamState.ogg_stream_packetin(header_comm);
		oggStreamState.ogg_stream_packetin(header_code);

		final ogg_page oggPage = new ogg_page();
		final ogg_packet decodingPacket = new ogg_packet();

		final OutputStream fileOutputStream = new FileOutputStream(
				destinationFile);

		// write header
		while (oggStreamState.ogg_stream_flush(oggPage)) {
			fileOutputStream.write(oggPage.header, 0, oggPage.header_len);
			fileOutputStream.write(oggPage.body, 0, oggPage.body_len);
		}

		final InputStream inputStream = new FileInputStream(tmpFile);
		byte[] readBuffer = new byte[(NUM_BYTES * 4) + 44];
		int numBytesRead, numBytesSubmitted;
		boolean eos = false;

		while (!eos) {
			// submit a chunk of audio data
			numBytesRead = inputStream.read(readBuffer, 0, NUM_BYTES * 4);
			if (numBytesRead == 0) {
				// EOF
				// handle the last frame, mark end of stream
				vorbisState.vorbis_analysis_wrote(0);
			} else {
				float[][] writeBuffer = vorbisState
						.vorbis_analysis_buffer(NUM_BYTES);

				// uninterleave samples
				for (numBytesSubmitted = 0; numBytesSubmitted < (numBytesRead / 4); numBytesSubmitted++) {
					writeBuffer[0][vorbisState.pcm_current + numBytesSubmitted] = ((readBuffer[(numBytesSubmitted * 4) + 1] << 8) | (0x00ff & readBuffer[numBytesSubmitted * 4])) / 32768.f;
					writeBuffer[1][vorbisState.pcm_current + numBytesSubmitted] = ((readBuffer[(numBytesSubmitted * 4) + 3] << 8) | (0x00ff & readBuffer[(numBytesSubmitted * 4) + 2])) / 32768.f;
				}

				vorbisState.vorbis_analysis_wrote(numBytesSubmitted);
			}

			// loop through available blocks
			while (vorbisBlock.vorbis_analysis_blockout(vorbisState)) {
				// use bitrate management
				vorbisBlock.vorbis_analysis(null);

				// submit the block and obtain packets
				vorbisBlock.vorbis_bitrate_addblock();
				while (vorbisState.vorbis_bitrate_flushpacket(decodingPacket)) {
					// output the packets obtained
					oggStreamState.ogg_stream_packetin(decodingPacket);

					while (!eos) {
						if (!oggStreamState.ogg_stream_pageout(oggPage)) {
							break;
						}

						fileOutputStream.write(oggPage.header, 0,
								oggPage.header_len);
						fileOutputStream.write(oggPage.body, 0,
								oggPage.body_len);

						if (oggPage.ogg_page_eos() > 0) {
							eos = true;
						}
					}
				}
			}
		}

		tmpFile.delete();
	}

}