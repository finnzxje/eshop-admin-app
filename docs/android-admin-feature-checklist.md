# Android Admin Feature Checklist

Copy this checklist into the task notes or pull request for every Android admin feature.

## Feature

- Name:
- Owner:
- Status:

## References

- React parity file:
- Backend doc:
- Backend controller or DTO, if used:
- Android reference:

## Implementation

- Activities added or changed:
- Layouts added or changed:
- Row layouts added or changed:
- Models added or changed:
- Adapters added or changed:
- API endpoints used:

## Rules Checked

- `ADMIN` visibility:
- `STAFF` visibility:
- Token stored in `SharedPreferences`:
- Protected requests send `Authorization: Bearer <token>`:
- `401` clears session and returns to login:
- `403` shows access denied:
- Empty state shown:
- Error state shown:
- Required form validation:
- Number/date parsing uses `try/catch`:

## Verification

- Build command:
- Build result:
- Unit test command, if used:
- Unit test result:
- Manual test notes:

## Handoff

- What works:
- Known limitations:
- Follow-up needed:
