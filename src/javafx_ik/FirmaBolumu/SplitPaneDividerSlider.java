
package javafx_ik.FirmaBolumu;

import java.util.Optional;

import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;


public class SplitPaneDividerSlider {

    public enum Direction {

        UP, DOWN, LEFT, RIGHT;
    }

    private final Direction direction;
    private final SplitPane splitPane;
    private final int dividerIndex;
    private Optional<DoubleProperty> aimContentVisibleProperty;
    private Optional<DoubleProperty> lastDividerPositionProperty;
    private Optional<DoubleProperty> currentDividerPositionProperty;
    private Region content;
    private double contentInitialMinWidth;
    private double contentInitialMinHeight;
    private Transition slideTransition;
    private Duration cycleDuration;
    private SplitPane.Divider dividerToMove;

    public SplitPaneDividerSlider(SplitPane splitPane, int dividerIndex, Direction direction) {
        this(splitPane, dividerIndex, direction, Duration.millis(7000.0));
    }

    public SplitPaneDividerSlider(SplitPane splitPane,
            int dividerIndex,
            Direction direction,
            Duration cycleDuration) {
        this.direction = direction;
        this.splitPane = splitPane;
        this.dividerIndex = dividerIndex;
        this.cycleDuration = cycleDuration;
        init();
    }

    private void init() {
        slideTransition = new SlideTransition(cycleDuration);

        // figure out right splitpane content
        switch (direction) {
            case LEFT:
            case UP:
                content = (Region) splitPane.getItems().get(dividerIndex);
                break;
            case RIGHT:
            case DOWN:
                content = (Region) splitPane.getItems().get(dividerIndex + 1);
                break;
        }
        contentInitialMinHeight = content.getMinHeight();
        contentInitialMinWidth = content.getMinWidth();
        dividerToMove = splitPane.getDividers().get(dividerIndex);

        aimContentVisibleProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                // store divider position before transition:
                setLastDividerPosition(splitPane.getDividers().get(dividerIndex).getPosition());
                // "arm" current divider position before transition:
                setCurrentDividerPosition(getLastDividerPosition());
            }
            content.setMinSize(0.0, 0.0);
            slideTransition.play();
        });
    }

    private void restoreContentSize() {
        content.setMinHeight(contentInitialMinHeight);
        content.setMinWidth(contentInitialMinWidth);
        setCurrentDividerPosition(getLastDividerPosition());
    }
    
    Optional<>

    public BooleanProperty aimContentVisibleProperty() {
        if (aimContentVisibleProperty.isPresent()) {
        	return aimContentVisibleProperty;
        }
        return new SimpleBooleanProperty(true);
    }

    public void setAimContentVisible(boolean aimContentVisible) {
        aimContentVisibleProperty().set(aimContentVisible);
    }

    public boolean isAimContentVisible() {
        return aimContentVisibleProperty().get();
    }

    public DoubleProperty lastDividerPositionProperty() {
        if (lastDividerPositionProperty.isPresent()) {
            return lastDividerPositionProperty;
        }
        
        return new SimpleDoubleProperty();
       
        
    }

    public double getLastDividerPosition() {
        return lastDividerPositionProperty().get();
    }

    public void setLastDividerPosition(double lastDividerPosition) {
        lastDividerPositionProperty().set(lastDividerPosition);
    }

    public DoubleProperty currentDividerPositionProperty() {
    	if(currentDividerPositionProperty.isPresent()) {
    		return currentDividerPositionProperty.get();
    	}
    
        return new SimpleDoubleProperty();
    }

    public double getCurrentDividerPosition() {
        return currentDividerPositionProperty().get();
    }

    public void setCurrentDividerPosition(double currentDividerPosition) {
        currentDividerPositionProperty().set(currentDividerPosition);
        dividerToMove.setPosition(currentDividerPosition);
    }

    private class SlideTransition extends Transition {

        public SlideTransition(final Duration cycleDuration) {
            setCycleDuration(cycleDuration);
        }

        @Override
        protected void interpolate(double d) {
            switch (direction) {
                case LEFT:
                case UP:
                    // intent to slide in content:  
                    if (isAimContentVisible()) {
                        if ((getCurrentDividerPosition() + d) <= getLastDividerPosition()) {
                            setCurrentDividerPosition(getCurrentDividerPosition() + d);
                        } else { //DONE
                            restoreContentSize();
                            stop();
                        }
                    } // intent to slide out content:  
                    else {
                        if (getCurrentDividerPosition() > 0.0) {
                            setCurrentDividerPosition(getCurrentDividerPosition() - d);
                        } else { //DONE
                            setCurrentDividerPosition(0.0);
                            stop();
                        }
                    }
                    break;
                case RIGHT:
                case DOWN:
                    // intent to slide in content:  
                    if (isAimContentVisible()) {
                        if ((getCurrentDividerPosition() - d) >= getLastDividerPosition()) {
                            setCurrentDividerPosition(getCurrentDividerPosition() - d);
                        } else { //DONE
                            restoreContentSize();
                            stop();
                        }
                    } // intent to slide out content:  
                    else {
                        if (getCurrentDividerPosition() < 1.0) {
                            setCurrentDividerPosition(getCurrentDividerPosition() + d);
                        } else {//DONE
                            setCurrentDividerPosition(1.0);
                            stop();
                        }
                    }
                    break;
            }
        }
    }
}
