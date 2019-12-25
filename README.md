SongJ
=====
<p align="center">
    <a href="LICENSE.md"><img src="https://badgen.net/github/license/blob/master/LICENSE.md" /></a>    
</p>
TODO: Description

## Table of Contents
 - [Features](#features)
 - [Usage](#usage)
    - [Usage Examples](#usage-examples)
 - [Supporting more files](#adding-support-for-additional-file-formats)
 - [Todo](#todo-list)
 - [License](#licensing)

## Features
* Command line interface
* Should be cross-platform. Currently only tested on Windows 8.1
* Built-in support for the following file formats: 
    * wav
    * aiff
    * au
    * midi
    * mp3
    * ogg

## Usage
<pre>
Usage: songj [options] File...
  FILE is a space-separated list of one or more audio files to try playing, 
  and can also include directories through which to traverse for audio files.
  
  Options:
    -d, --traverse
      Traverse through any directory arguments for additional audio files to play.
      Default: false
    
    --max_depth &lt;value&gt;
      Maximum number of directory levels to search.
      Default: 2147483647
    
    --color
      Enables colored output
      Default: false
    
    --list_only
      Whether to only display the final play queue, and not attempt to play anything.
      Default: false
      
    -h, --help
      Displays this help message
</pre>
      
### Usage Examples
TODO: Provide examples

## Adding support for additional file formats

To add a new player class:
1.  The new class must extend either `AbstractAudioPlayer` or `SpecializedAudioPlayer` as applicable
    and implement any required methods.
2.  The new class must include a static block which makes a call to `PlayerTypeMap.insert()`
    The first parameter of this call is to be an instance of the new class.
    The second parameter is a `Set<String>` of the mime content-types the class supports.
3.  A key-value pair for the new class must be added to the `playerHandlers.properties` file.
    The key can be anything as long as it's unique, but the value must be the fully qualified name of the new class.

Here is an example of a new audio player class
```java
public class MyAudioPlayerClass extends AbstractAudioPlayer {

    protected static final Set<String> supportedTypes = new HashSet<>(Arrays.asList("TYPE1", "TYPE2"));

	@Override
	public Set<String> getSupportedTypes() { return supportedTypes;}

	static { PlayerTypeMap.insert(new MyAudioPlayerClass(), supportedTypes);}

	public MyAudioPlayerClass() {}

	public void playFile(File file) throws UnsupportedAudioFileException {
            /* Implementation */
	}
```


## TODO List
 - [ ] Test on more platforms
 - [ ] Finish README
    - [ ] Add project description
    - [ ] Provide example usages 
 - [ ] Releases
 
 - [ ] Fix file globbing
 - [ ] Fix inconsistencies with milliseconds
 - [ ] Fix `get` and `remove` methods in `PlayerTypeMap`
 - [ ] `ArgManager` should complain about unknown options
 
 - [ ] Find out if `VorbisAudioPlayer` will also properly handle flac, theora, speex, and opus files
 - [ ] Test playback and quality of aifc, 8svx, MThd, MTrk, and anything else that .aiff, .au, and .mid can be
 - [ ] Document `VorbisAudioPlayer`, `Mp3AudioPlayer`, `PlayerTypeMap`
 - [ ] Document `FileUtils.magicTest`
 
 - [ ] Switch `BasicAudioPlayer` from using `Clip` to using `DataLine`
 - [ ] Set program description value in `SongJ.java`
 - [ ] Use listConverter for main parameter in `CLIArguments`
 - [ ] Implement validation for --maxDepth parameter
 
 - [ ] Add support for shuffling the queue using `Collections.shuffle`
 - [ ] Provide bash completion
 - [ ] See if its feasible to provide a means of converting an arbitrary audio file to a (roughly) equivalent midi file*/
 - [ ] Implement play, pause, next, and previous file navigation
  
 
## Licensing
This project is licensed under the MIT License.