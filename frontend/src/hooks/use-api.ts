"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { ApiError } from "@/types";

interface ApiState<T> {
  data: T | null;
  error: ApiError | null;
  loading: boolean;
}

export function useApi<T>(fetcher: () => Promise<T>, deps: unknown[] = []) {
  const [state, setState] = useState<ApiState<T>>({ data: null, error: null, loading: true });
  const [tick, setTick] = useState(0);
  const fetcherRef = useRef(fetcher);
  fetcherRef.current = fetcher;

  useEffect(() => {
    let active = true;
    setState((previous) => ({ ...previous, loading: true, error: null }));
    fetcherRef
      .current()
      .then(
        (data) => {
          if (active) setState({ data, error: null, loading: false });
        },
        (error: unknown) => {
          if (!active) return;
          setState({
            data: null,
            error:
              error instanceof ApiError
                ? error
                : new ApiError(0, error instanceof Error ? error.message : String(error)),
            loading: false,
          });
        }
      );
    return () => {
      active = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, tick]);

  const reload = useCallback(() => setTick((value) => value + 1), []);

  return { ...state, reload };
}