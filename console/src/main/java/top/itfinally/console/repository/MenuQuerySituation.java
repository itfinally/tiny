package top.itfinally.console.repository;

import top.itfinally.core.repository.BasicQuerySituation;

import java.util.Map;

@SuppressWarnings( "unchecked" )
public class MenuQuerySituation extends BasicQuerySituation {
  // direct parent or first born child
  protected boolean isDirect;

  protected static class InnerBuilder<SituationType extends MenuQuerySituation, BuilderType
      extends MenuQuerySituation.InnerBuilder<SituationType, BuilderType>>
      extends BasicQuerySituation.InnerBuilder<SituationType, BuilderType> {

    private boolean isDirect = false;

    protected InnerBuilder() {
    }

    protected InnerBuilder( Map<String, Object> conditions ) {
      super( conditions );
    }

    public BuilderType setDirect( boolean direct ) {
      isDirect = direct;
      return ( BuilderType ) this;
    }

    @Override
    protected SituationType build( SituationType situation ) {
      situation.isDirect = isDirect;

      return super.build( situation );
    }
  }

  public static class Builder extends InnerBuilder<MenuQuerySituation, Builder> {
    public Builder() {
    }

    public Builder( Map<String, Object> conditions ) {
      super( conditions );
    }

    public MenuQuerySituation build() {
      return super.build( new MenuQuerySituation() );
    }
  }

  protected MenuQuerySituation() {
  }

  public boolean isDirect() {
    return isDirect;
  }
}
