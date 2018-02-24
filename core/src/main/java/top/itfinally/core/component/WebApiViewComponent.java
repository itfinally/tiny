package top.itfinally.core.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.itfinally.core.util.RestUrlScanHelper;
import top.itfinally.core.vo.ApiViewVoBean;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.core.vo.CollectionResponseVoBean;

import java.util.*;

import static top.itfinally.core.enumerate.ResponseStatusEnum.EMPTY_RESULT;
import static top.itfinally.core.enumerate.ResponseStatusEnum.SUCCESS;

public abstract class WebApiViewComponent {
  private List<ApiViewVoBean> apiViewVoBeans;
  private Logger logger = LoggerFactory.getLogger( getClass() );

  {
    logger.info( String.format( "ready to scan %s api.", getClass().getName() ) );

    this.apiViewVoBeans = Collections.unmodifiableList(
        new RestUrlScanHelper( getClass() ).doScan( getClass() )
    );

    logger.info( String.format( "create %s api view successful.", getClass().getName() ) );
  }

  @ResponseBody
  @GetMapping( "/api" )
  public BaseResponseVoBean api() {
    if ( apiViewVoBeans != null ) {
      return new CollectionResponseVoBean<ApiViewVoBean>( SUCCESS ).setResult( apiViewVoBeans );
    }

    return new CollectionResponseVoBean<>( EMPTY_RESULT ).setResult( new ArrayList<>() );
  }
}
