package com.prj.tuning.xdf.olsimpl;

import java.util.Arrays;
import java.util.List;

import com.prj.tuning.mappack.map.Axis;
import com.prj.tuning.xdf.binding.XdfAxis;
import com.prj.tuning.xdf.binding.XdfCategoryMem;
import com.prj.tuning.xdf.binding.XdfEmbedded;
import com.prj.tuning.xdf.binding.XdfMath;
import com.prj.tuning.xdf.binding.XdfTable;
import com.prj.tuning.xdf.olsimpl.addressmap.AddressMap;

public class OlsAxisMap extends XdfTable {
  public static final int AXIS_CATEGORY = 255;
  private Axis axis;
  private AddressMap sub;

  public OlsAxisMap(Axis axis, AddressMap sub) {
    this.axis = axis;
    this.sub = sub;
  }

  @Override
  public String getUniqueId() {
    return String.format(OlsProject.ADDRESS_FORMAT, sub.subAddr(axis.getAddress()));
  }

  @Override
  public String getTitle() {
    return getUniqueId() + " - " + axis.getValue().getUnits();
  }

  @Override
  public XdfAxis getRowAxis() {
    return new XdfAxis() {

      @Override
      public String getDimension() {
        return "y";
      }

      @Override
      public Integer getXdfIndexcount() {
        return axis.getLength();
      }
    };
  }

  @Override
  public XdfAxis getColAxis() {
    return new XdfAxis() {

      @Override
      public String getDimension() {
        return "x";
      }

      @Override
      public Integer getXdfIndexcount() {
        return 1;
      }
    };
  }

  @Override
  public XdfAxis getValueAxis() {
    return new XdfAxis() {

      @Override
      public String getDimension() {
        return "z";
      }

      @Override
      public Integer getDecimalpl() {
        return axis.getPrecision() > 2 ? 2 : (int)axis.getPrecision();
      }

      @Override
      public XdfEmbedded getEmbedded() {
        return new XdfEmbedded() {

          @Override
          public int getXdfWidth() {
            return axis.getValueType().width * 8;
          }

          @Override
          public String getXdfAddress() {
            return getUniqueId();
          }

          @Override
          public String getTypeFlags() {
            return String.format("0x%X", (axis.isSigned() ? 1 : 0) + (axis.getValueType().isLsbFirst() ? 2 : 0));
          }
        };
      }

      @Override
      public List<XdfMath> getXdfMath() {
        return Arrays.asList(new XdfMath[] {new OlsMath(axis.getValue())});
      }
    };
  }

  @Override
  public XdfCategoryMem getCategory() {
    return new OlsCategoryMem(AXIS_CATEGORY);
  }
}
