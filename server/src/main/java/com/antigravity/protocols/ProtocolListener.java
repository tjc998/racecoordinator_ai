package com.antigravity.protocols;

import com.antigravity.proto.InterfaceEvent;
import com.antigravity.proto.InterfaceStatus;

public interface ProtocolListener {

  void onLap(int lane, double lapTime, int interfaceId);

  void onSegment(int lane, double segmentTime, int interfaceId);

  void onCallbutton(int lane);

  void onInterfaceStatus(InterfaceStatus status);

  void onCarData(CarData carData);

  void onInterfaceEvent(InterfaceEvent event);
}
