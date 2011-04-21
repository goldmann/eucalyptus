package com.eucalyptus.blockstorage;

import net.sf.json.JSONException;
import com.eucalyptus.auth.AuthException;
import com.eucalyptus.auth.policy.PolicySpec;
import com.eucalyptus.auth.policy.key.KeyUtils;
import com.eucalyptus.auth.policy.key.Keys;
import com.eucalyptus.auth.policy.key.PolicyKey;
import com.eucalyptus.auth.policy.key.QuotaKey;
import com.eucalyptus.images.ImageUtil;

@PolicyKey( Keys.EC2_QUOTA_VOLUME_NUMBER )
public class VolumeNumberQuotaKey extends QuotaKey {
  
  private static final String KEY = Keys.EC2_QUOTA_VOLUME_NUMBER;
  
  @Override
  public void validateValueType( String value ) throws JSONException {
    KeyUtils.validateIntegerValue( value, KEY );
  }
  
  @Override
  public boolean canApply( String action, String resourceType ) {
    if ( PolicySpec.qualifiedName( PolicySpec.VENDOR_EC2, PolicySpec.EC2_CREATEVOLUME ).equals( action ) &&
         PolicySpec.qualifiedName( PolicySpec.VENDOR_EC2, PolicySpec.EC2_RESOURCE_VOLUME ).equals( resourceType ) ) {
      return true;
    }
    return false;
  }
  
  @Override
  public String value( Scope scope, String id, String resource, Long quantity ) throws AuthException {
    switch ( scope ) {
      case ACCOUNT:
        return Long.toString( StorageUtil.countVolumeByAccount( id ) + 1 );
      case GROUP:
        throw new AuthException( "Group level quota not supported" );
      case USER:
        return Long.toString( StorageUtil.countVolumeByUser( id ) + 1 );
    }
    throw new AuthException( "Invalid scope" );
  }
  
}