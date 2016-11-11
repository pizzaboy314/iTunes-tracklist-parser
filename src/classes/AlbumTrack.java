package classes;

public class AlbumTrack implements Comparable<AlbumTrack> {
	
	public Integer discNum;
	public Integer trackNum;
	public String trackTitle;
	public String trackDuration;
	
	public AlbumTrack(Integer discNum, Integer trackNum, String trackTitle, String trackDuration){
		this.discNum = discNum;
		this.trackNum = trackNum;
		this.trackTitle = trackTitle;
		this.trackDuration = trackDuration;
	}

	public Integer getDiscNum() {
		return discNum;
	}

	public void setDiscNum(Integer discNum) {
		this.discNum = discNum;
	}

	public Integer getTrackNum() {
		return trackNum;
	}

	public void setTrackNum(Integer trackNum) {
		this.trackNum = trackNum;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}

	public String getTrackDuration() {
		return trackDuration;
	}

	public void setTrackDuration(String trackDuration) {
		this.trackDuration = trackDuration;
	}
	
	// Comparator object implementation to make AlbumTrack sortable by disc # and track #
	@Override
	public int compareTo(AlbumTrack at) {
		int compare = discNum - at.discNum;
		if(compare != 0){
			return compare;
		} else {
			return trackNum - at.trackNum;
		}
	}
}
